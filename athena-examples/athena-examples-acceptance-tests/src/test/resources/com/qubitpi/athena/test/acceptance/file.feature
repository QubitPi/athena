Feature: File

  Scenario: A file can be uploaded and a file ID is returned
    When a text file is uploaded
    Then the ID of that file is returned and the file metadata is generated
    When the file ID of an existing text file is provided to download
    Then the text can be properly downloaded
