import json
import os
from datetime import datetime, timezone

from dotenv import load_dotenv
from fs_tools import list_files
from fs_tools import read_file
from fs_tools import search_in_directory
from fs_tools import search_in_file
from fs_tools import write_file

from openai import OpenAI


MAX_TOOL_OUTPUT_CHARS = 12000
MAX_TOOL_CALL_ROUNDS = 80
ENV_FILE = ".env"
TOOLS_LOG_FILE = "tools_log.txt"
MAX_LOG_VALUE_CHARS = 500
MAX_LOG_RESULT_CHARS = 3000


SYSTEM_PROMPT = """
You are an LLM file assistant.
Use the available tools to inspect, search, and write files when the user asks.

Important rules:
- Prefer tool calls over guessing about file contents.
- For folder-wide keyword searches, use search_in_directory.
- For folder-wide reading tasks, call list_files first, then call read_file on relevant files.
- Supported readable file types are txt, pdf, and docx.
- Supported writable file types are txt and docx. PDFs cannot be edited by write_file.
- When creating a summary file for a PDF, read the PDF first, summarize it, then write the summary to a .txt or .docx file.
- Keep final answers concise and mention which files were used or created.
""".strip()


TOOLS = [
    {
        "type": "function",
        "function": {
            "name": "list_files",
            "description": "List files in a directory, optionally filtered by file extension.",
            "parameters": {
                "type": "object",
                "properties": {
                    "directory": {
                        "type": "string",
                        "description": "Directory path to inspect, such as 'resumes'.",
                    },
                    "extension": {
                        "type": "string",
                        "description": "Optional extension filter, such as 'pdf', 'docx', or '.txt'.",
                    },
                },
                "required": ["directory"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "read_file",
            "description": "Read text content and metadata from a txt, pdf, or docx file.",
            "parameters": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Path to the file to read.",
                    },
                },
                "required": ["path"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "search_in_file",
            "description": "Search for a keyword in a txt, pdf, or docx file. Search is case-insensitive and returns surrounding context.",
            "parameters": {
                "type": "object",
                "properties": {
                    "filepath": {
                        "type": "string",
                        "description": "Path to the file to search.",
                    },
                    "keyword": {
                        "type": "string",
                        "description": "Keyword to search for.",
                    },
                },
                "required": ["filepath", "keyword"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "search_in_directory",
            "description": "Search all readable txt, pdf, and docx files in a directory for a keyword. Search is case-insensitive and returns matches grouped by file.",
            "parameters": {
                "type": "object",
                "properties": {
                    "directory": {
                        "type": "string",
                        "description": "Directory path to search, such as 'resumes'.",
                    },
                    "keyword": {
                        "type": "string",
                        "description": "Keyword to search for.",
                    },
                },
                "required": ["directory", "keyword"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "write_file",
            "description": "Write content to a txt or docx file. PDF writes return a cannot-edit response.",
            "parameters": {
                "type": "object",
                "properties": {
                    "path": {
                        "type": "string",
                        "description": "Path where content should be written.",
                    },
                    "content": {
                        "type": "string",
                        "description": "Content to write into the file.",
                    },
                    "overwrite": {
                        "type": "boolean",
                        "description": "Whether to overwrite an existing file.",
                        "default": True,
                    },
                    "create_parent_dirs": {
                        "type": "boolean",
                        "description": "Whether to create parent directories if they do not exist.",
                        "default": True,
                    },
                },
                "required": ["path", "content"],
            },
        },
    },
]


def compact_tool_output(result):
    text = json.dumps(result, indent=2, ensure_ascii=False, default=str)

    if len(text) <= MAX_TOOL_OUTPUT_CHARS:
        return text

    return (
        text[:MAX_TOOL_OUTPUT_CHARS]
        + "\n...tool output truncated because it was too large..."
    )


def shorten_for_log(value):
    if isinstance(value, str):
        if len(value) <= MAX_LOG_VALUE_CHARS:
            return value

        return value[:MAX_LOG_VALUE_CHARS] + "...truncated..."

    if isinstance(value, dict):
        return {
            key: shorten_for_log(item)
            for key, item in value.items()
        }

    if isinstance(value, list):
        return [
            shorten_for_log(item)
            for item in value
        ]

    return value


def log_tool_use(tool_name, arguments, result, tool_call_id=None):
    result_preview = compact_tool_output(result)
    if len(result_preview) > MAX_LOG_RESULT_CHARS:
        result_preview = result_preview[:MAX_LOG_RESULT_CHARS] + "...truncated..."

    log_entry = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "tool_call_id": tool_call_id,
        "tool_name": tool_name,
        "arguments": shorten_for_log(arguments),
        "result_preview": result_preview,
    }

    with open(TOOLS_LOG_FILE, "a", encoding="utf-8") as log_file:
        log_file.write(json.dumps(log_entry, ensure_ascii=False, default=str))
        log_file.write("\n")


def load_env_file(path=ENV_FILE):
    load_dotenv(path)


def call_local_tool(tool_name, arguments):
    tool_map = {
        "list_files": list_files,
        "read_file": read_file,
        "search_in_directory": search_in_directory,
        "search_in_file": search_in_file,
        "write_file": write_file,
    }

    if tool_name not in tool_map:
        return {"error": f"Unknown tool: {tool_name}"}

    try:
        return tool_map[tool_name](**arguments)
    except Exception as error:
        return {
            "error": str(error),
            "error_type": type(error).__name__,
        }


def parse_tool_arguments(raw_arguments):
    try:
        return json.loads(raw_arguments or "{}"), None
    except json.JSONDecodeError as error:
        return None, {
            "error": f"Invalid tool arguments: {raw_arguments}",
            "error_type": type(error).__name__,
        }


def new_openai_message_to_dict(message):
    assistant_message = {
        "role": "assistant",
        "content": message.content,
    }

    if message.tool_calls:
        assistant_message["tool_calls"] = [
            tool_call.model_dump() for tool_call in message.tool_calls
        ]

    return assistant_message


def get_client_and_model(model=None):
    load_env_file()

    if not os.getenv("OPENAI_API_KEY"):
        raise RuntimeError("OPENAI_API_KEY is not set.")

    base_url = os.getenv("OPENAI_BASE_URL", "http://localhost:11434/v1")
    client = OpenAI(base_url=base_url)
    selected_model = model or os.getenv("OPENAI_MODEL")

    if not selected_model:
        raise RuntimeError("OPENAI_MODEL is not set.")

    return client, selected_model


def create_message_history():
    return [
        {"role": "system", "content": SYSTEM_PROMPT},
    ]


def run_assistant_turn(client, model, messages, user_query):
    messages.append({"role": "user", "content": user_query})

    for _ in range(MAX_TOOL_CALL_ROUNDS):
        response = client.chat.completions.create(
            model=model,
            messages=messages,
            tools=TOOLS,
            tool_choice="auto",
        )
        message = response.choices[0].message
        messages.append(new_openai_message_to_dict(message))

        if not message.tool_calls:
            return message.content

        for tool_call in message.tool_calls:
            tool_name = tool_call.function.name
            arguments, tool_result = parse_tool_arguments(tool_call.function.arguments)

            if tool_result is None:
                tool_result = call_local_tool(tool_name, arguments)

            log_tool_use(tool_name, arguments or {}, tool_result, tool_call.id)

            messages.append({
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": compact_tool_output(tool_result),
            })

    return "I reached the tool-call limit before finishing the request."


def run_assistant(user_query, model=None):
    client, selected_model = get_client_and_model(model=model)
    messages = [
        {"role": "system", "content": SYSTEM_PROMPT},
    ]

    return run_assistant_turn(client, selected_model, messages, user_query)


def run_chat():
    client, model = get_client_and_model()
    messages = create_message_history()

    print("File assistant chat started. Type 'exit' or 'quit' to stop.")

    while True:
        user_query = input("\nYou: ").strip()

        if user_query.lower() in {"exit", "quit"}:
            print("Goodbye.")
            break

        if not user_query:
            continue

        answer = run_assistant_turn(client, model, messages, user_query)
        print(f"\nAssistant: {answer}")


def main():
    run_chat()


if __name__ == "__main__":
    main()
