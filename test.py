from fs_tools import list_files
from fs_tools import read_file
from fs_tools import search_in_directory
from fs_tools import search_in_file


def print_file_summary(filepath):
    result = read_file(filepath)

    print(f"\nFile: {filepath}")
    print(f"Type: {result['file_type']}")
    print(f"Reader: {result['reader']}")
    print(f"Text length: {result['text_length']}")
    print(f"Word count: {result['word_count']}")


def print_search_results(filepath, keyword):
    result = search_in_file(filepath, keyword)

    print(f"\nSearching for '{keyword}' in {filepath}")
    print(f"Matches found: {result['match_count']}")

    for index, match in enumerate(result["matches"], start=1):
        expected_full_match = (
            match["pre_context"]
            + match["match"]
            + match["post_context"]
        )

        print(f"\nMatch {index}")
        print(f"Matched text: {match['match']}")
        print(f"Start index: {match['start_index']}")
        print(f"End index: {match['end_index']}")
        print(f"Pre-context: {match['pre_context']}")
        print(f"Post-context: {match['post_context']}")
        print(f"Full match: {match['full_match']}")
        print(f"Full match is contiguous: {expected_full_match == match['full_match']}")


def print_directory_search_results(directory, keyword):
    result = search_in_directory(directory, keyword)

    print(f"\nSearching for '{keyword}' in directory {directory}")
    print(f"Files searched: {result['searched_files']}")
    print(f"Files with matches: {result['matched_files']}")
    print(f"Total matches: {result['total_matches']}")

    for file_result in result["results"]:
        print(f"- {file_result['filepath']}: {file_result['match_count']} matches")


print("Files in dummy_files:")
print(list_files("dummy_files"))

print_file_summary("dummy_files/sample.txt")
print_file_summary("dummy_files/sample.docx")
print_file_summary("dummy_files/sample.pdf")

print_search_results("dummy_files/sample.txt", "python")
print_search_results("dummy_files/sample.txt", "LLM")
print_search_results("dummy_files/sample.docx", "reader")
print_search_results("dummy_files/sample.pdf", "project")
print_directory_search_results("dummy_files", "python")
