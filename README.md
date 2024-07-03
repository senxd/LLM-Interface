# LLM-Interface

_This project is a work in progress and is not expected to use in a production programs._

This project provides a framework for utilizing LLMs in a structured way in Kotlin through the use of DSLs. You can
either bring your own LLM by extending the `LLM` class or use a pre-existing such as `OpenAIModel`.

# Features
_This list is non-exhaustive and subject to change._
- [x] Basic prompting (send message, receive message)
- [x] Message History in DSL
- [ ] LLM Features
  - [ ] API
    - [x] Functions (Allows for LLM to use defined functions, e.g., RAG or Web Search, or custom)
    - [ ] Database API
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
- [x] Document
  - [x] Parsing
    - [x] PDFs
      - [x] OCR
      - [x] Text Metadata Extraction
    - [x] Generic Text
      - [x] Plain Text _.txt_
      - [x] Microsoft Word _.docx_ or _.doc_
      - [x] Rich Text Format _.rtf_
- [ ] Batch Processing
  - [x] PDFs
  - [ ] LLM Requests

# Related Research Papers
[ReAct, method for improving accuracy complex tasks](https://arxiv.org/abs/2210.03629)

[Self-Consistency Improves ... - Improved CoT Reasoning](https://arxiv.org/abs/2203.11171)

# Future Demos & Research
- [ ] Demos
  - [ ] Document Tagging Demo
- [ ] Research
  - [ ] Testing a combined ReAct + Observational Sub-stepping Approach with different datasets:
    - [ ] ALFWorld
    - [ ] WebShop