# LLM-Interface

_This project is a work in progress and is not expected to use in a production programs._

This project provides a framework for utilizing LLMs in a structured way in Kotlin through the use of DSLs. You can
either bring your own LLM by extending the `Model` class or use a pre-existing one for OpenAI (among others).

# Features
_This list is non-exhaustive and subject to change._
- [x] Basic prompting (send message, receive message)
- [ ] Message History in DSL
- [ ] LLM Features
  - [x] API Functions (Allows for LLM to use defined functions, e.g., RAG or Web Search, or custom)
  - [x] Sequence Processing (_CoT_)
    - [ ] Generating a list of steps to solve a given problem
      - [ ] Executing said list with different API tools
  - [ ] Image Processing
  - [ ] Embedding Support (OpenAI Model, Generalizable Functions)
    - [ ] Generalizable Functions
    - [ ] OpenAI Model
  - [ ] Extracting key information from large documents
- [ ] Observer (Tools to monitor LLM performance in automated sequence tasks)
  - [ ] Metric Based Observation
  - [ ] LLM Based Observation
- [ ] Document
  - [ ] Parsing
    - [x] PDFs
      - [x] OCR
      - [x] Text Metadata Extraction
    - [ ] Generic Text
      - [ ] Plain Text _.txt_
      - [ ] Microsoft Word _.docx_
      - [ ] Rich Text Format _.rtf_
- [ ] Batch Processing
  - [x] PDFs
  - [ ] LLM Requests