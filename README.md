# Jan Plan Scheduler

A preference-based course scheduler for Yale's January "Jam Plan" term. Given a CSV of student enrollment preferences, it assigns every student to either one full-day class or two complementary half-day classes, maximizing preference satisfaction while respecting class capacities and grade-level priority.

---

## Background

Jan Plan runs as a one-month intensive term where students select courses across two formats:

- **Full-day classes** — one course for the month (capacity: 15 students)
- **Half-day classes** — two courses, one morning and one afternoon (capacity: 30 students each)

Students rank up to 10 full-day and 10 half-day courses in order of preference. The scheduler attempts to honor those preferences while ensuring seniors (grade 12) are prioritized in high-demand courses.

---

## Algorithm

Scheduling runs in three passes, applied separately to full-day and half-day tracks:

**Pass 1 — Top-5 preferences, preferred format only**
For each class, a priority queue ranks unassigned students by grade (seniors first) then by preference rank. Students are added only if the course appears in their top 5 preferences. This pass runs five times, tightening preference thresholds each round.

**Pass 2 — Overflow handling**
Any student not placed in Pass 1 (because their top-5 preferred-format courses are full) is considered for any class regardless of format preference, still using the priority queue ordering.

**Pass 3 — Final placement**
Remaining unplaced students are assigned to any class with open capacity, without preference constraints, ensuring everyone receives a schedule.

After all passes, the scheduler reports average preference rank achieved across full-day and half-day placements.

---

## Project Structure

```
Jam-Plan-Scheduler/
├── Runner.java      # Entry point; runs the three scheduling passes and reports results
├── Schedule.java    # Parses the input CSV, initializes students and classes
├── Student.java     # Student model: name, ID, grade, ranked preferences, half-day count
├── Class.java       # Class model: ID, capacity, format (full/half), enrolled students
├── PQueue.java      # Custom sorted array-backed priority queue (grade → preference rank)
├── pseudo.txt       # High-level pseudocode overview
├── Dockerfile       # Ubuntu + OpenJDK 19 container for running without local Java setup
├── test.csv         # Auto-generated synthetic dataset (800 students, randomized prefs)
├── res.csv          # Sample output: student assignments per class
└── Sample Jan Plan enrollment spreadsheet - Student Info (1).csv  # Real-format reference
```

---

## Input CSV Format

The input CSV must follow this column order:

| Column(s) | Content |
|---|---|
| 0 | Student last name |
| 1 | Student first name |
| 2 | Student ID |
| 3 | Grade level (9–12) |
| 4 | Format preference (`full` or `half`) |
| 5 … 5+N | Full-day course preferences (ranked 1–10, blank if unranked) |
| 5+N … end | Half-day course preferences (ranked 1–10, blank if unranked) |

The number of full-day courses `N` is passed to the `Schedule` constructor (currently set to `26` in `Runner.java`). Half-day course columns follow immediately after.

A sample spreadsheet with the correct structure is included as reference.

---

## Usage

### Option 1 — Docker (no local Java required)

```bash
docker build -t jam-plan .
docker run -it jam-plan
# then compile and run inside the container:
javac *.java
java Runner
```

### Option 2 — Local Java (JDK 19+)

```bash
javac *.java
java Runner
```

### Changing the Input File

In `Runner.java`, update the `Schedule` constructor to point at your CSV and specify the number of full-day courses:

```java
Schedule jamPlan = new Schedule("your_file.csv", 26);
```

### Generating a Synthetic Test Dataset

`createTestCSV()` in `Runner.java` generates an 800-student CSV with randomized preferences. Uncomment the call in `main` and update the output path to use it.

### Exporting Results

`createResultCSV()` writes the final class rosters to `res.csv`. Uncomment the call in `main` and update the output path to export results.

---

## Output

The program prints average preference rank achieved for placed students:

```
average preferences
full: 2.47
half: 3.12
```

Lower values indicate students were placed in courses closer to their top choice. Students placed outside their ranked preferences (Pass 3 fallbacks) are excluded from the average.

---

## Known Limitations & Future Work

- **Conflict checking** — `Student.alreadyTaken` is stubbed out but not yet implemented; students could be placed in courses they've previously completed.
- **Priority queue** — The current implementation is a sorted insertion array (O(n) insert). A heap-based implementation would improve performance at larger scale.

---

## License

MIT © 2025 Joseph Davis
