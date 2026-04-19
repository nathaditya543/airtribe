# LLM File Assistant

An OpenAI-compatible file assistant that lets an LLM inspect local files through Python tools. The assistant can read supported documents, search inside files or directories, write text and DOCX outputs, and keep a log of tool calls made by the model.

## Features

- Read `.txt`, `.pdf`, and `.docx` files.
- List files in a directory.
- Search for keywords inside a single file.
- Search all readable files in a directory.
- Write `.txt` and `.docx` files.
- Refuse PDF writes instead of creating invalid PDF files.
- Chat with the model in a persistent terminal session.
- Log model tool usage to `tools_log.txt`.

## Project Files

- `fs_tools.py`: Local filesystem tools used by the assistant.
- `llm_file_assistant.py`: Chat loop and OpenAI-compatible tool-calling integration.
- `test.py`: Direct tests and demos for the filesystem tools.
- `dummy_files/`: Small sample files for basic testing.
- `resume_pdfs/`: Resume PDF samples for directory search and summarization tests.
- `.env`: Local environment variables. This file is ignored by Git.
- `tools_log.txt`: Runtime tool-call log. This file is ignored by Git.

## Setup

Install dependencies:

```powershell
python -m pip install openai python-dotenv pypdf python-docx
```

Create a `.env` file:

```env
OPENAI_API_KEY=your_api_key_here
OPENAI_MODEL=your_model_here
```

The project currently creates the client with:

```python
OpenAI(base_url="http://localhost:11434/v1")
```

That works for OpenAI-compatible local endpoints such as Ollama. If you want to use OpenAI directly, remove the custom `base_url` from `llm_file_assistant.py` and set `OPENAI_MODEL` to an OpenAI model.

## Run Chat

Start the assistant:

```powershell
python llm_file_assistant.py
```

Then ask file questions:

```text
Find resumes mentioning Python experience.
Read Resume_CV Samples-12.pdf.
Create a summary file for Resume_CV Samples-12.pdf.
Search dummy_files for LLM.
```

Type `exit` or `quit` to stop the chat.

## Run Tool Tests

```powershell
python test.py
```

This exercises reading, listing, file search, and directory search without calling the LLM.

## Available Tools

### `list_files(directory, extension=None)`

Lists files in a directory. An optional extension filter can limit results.

### `read_file(path)`

Reads `.txt`, `.pdf`, or `.docx` content and returns text plus metadata.

### `write_file(path, content, overwrite=True, create_parent_dirs=True)`

Writes content to `.txt` or `.docx`. If the path is a PDF, it returns a cannot-edit response.

### `search_in_file(filepath, keyword)`

Performs case-insensitive keyword search in one readable file. Each match includes:

- `pre_context`
- `match`
- `post_context`
- `full_match`
- start and end indexes

### `search_in_directory(directory, keyword)`

Searches all readable files in a directory by wrapping `search_in_file`.

## Tool Logs

Each model-triggered tool call is appended to `tools_log.txt` as JSON Lines. A log entry includes:

- timestamp
- tool call id
- tool name
- arguments
- compact result preview

`tools_log.txt` is ignored by Git because it is local runtime output.

## Notes

- `.env` is ignored by Git so API keys stay local.
- The assistant keeps conversation history during a chat session.
- Directory search is currently one level deep and does not recurse into subdirectories.
