# Feedback PL3 Thunder Wolf Squadron

|  | Product Planning |  |  |  |  |
|-------------------------------------|------------------|----------------|---------------|---------|---------|
|  |  |  | User Stories |  |  |
|  | Analysis | Prioritisation | Specification | Clarity | Roadmap |
| PL3 (Thunder Wolf Squadron) | 8 | 8 | 8 | 8 | 8 |

|  | Sprint 2 |  |  |  |  |  |
|-------------------------------------|------------|-----------|----------------|-----------------------|----------------|------------|
|  | User Story |  |  | Learning From History |  |  |
| Group | Definition | Splitting | Responsibility | Estimation | Prioritisation | Reflection |
| PL3 (Thunder Wolf Squadron) | 8 | 5 | 6 | 8 | 7 | 6 |

|  | Code Evolution Quality 2 |  |  |  |  |  |  |  |  |  |  |
|-------------------------------------|--------------------------|---------|---------------------|------------------|--------|----------|------------------------|---------|---------|------------------------|-------------|
|  | Architecture |  |  | Code Readability |  |  | Continuous Integration |  |  | Pull-based Development |  |
| Group | Changes | Arch-DD | Code Change Quality | Formatting | Naming | Comments | Building | Testing | Tooling | Branching | Code Review |
| PL3 (Thunder Wolf Squadron) | 7 | 6 | 8 | 8 | 8 | 9 | 6 | 4 | 3 | 8 | 8 |

## New Feedback From After Meeting
- Changed testing grade to 4, as discussed
- EAD Feedback
- Check Code Quality

### EAD Feedback
- Should be a formal document. (Layout, add front cover with teammembers and so on)
- Describe shortly what your software will do.
- Reread 'availability', lots of useless text
- In 'Manageability', you're supposed to talk about the creation. Refactoring after the creation seems weird.
- In 'Performance', explain what elements of the main problem cause these performance issues and what elements will be resolved by multithreading and preprocessing.
- In 'Performance' also discuss the size of these files and the impact on performance.
- In 'Scalability', actually tell how you're going to solve this problem, there is no 'not much that can be done'
- Again, in 'Security' tell the how. 
- In the subsystem, are there two different users? Not clear in the story.
- Kind of a conflict in the Database being an individual model when it runs with the program

### Code Quality Feedback
- Building a Logger is more secure
- Lack of design patterns (MVC for example)
- Right now GUI loads, parses and retrieves the data
- Useless MainController
 
 In this point of the rubrics there will always be something that I'm missing.

Please prepare next SE meeting with questions.