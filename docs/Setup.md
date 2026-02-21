# MediTrack Setup Guide

## Prerequisites
- Java JDK 8+ installed (`javac` and `java` available in PATH)
- Windows PowerShell (or any terminal that can run Java commands)

## Project Structure (High Level)
- `entities/`: domain models (`Doctor`, `Patient`, `Appointment`, `Bill`, etc.)
- `services/`: business logic and CSV persistence
- `exceptions/`: custom runtime exceptions
- `tests/`: lightweight test suite + test runner
- `data/`: CSV files created at runtime for persistence

## How To Build
From project root (`d:\meditrack`):

```powershell
javac Main.java entities\*.java services\*.java exceptions\*.java tests\*.java
```

## How To Run The App

```powershell
java Main
```

You will get a menu-driven CLI to:
- add doctors/patients
- set doctor schedules (during doctor creation)
- book appointments (validated by `ScheduleService`)
- generate/pay bills
- view bill summaries

## How To Run Tests

```powershell
javac Main.java entities\*.java services\*.java exceptions\*.java tests\*.java
java tests.TestRunner
```

The test runner prints per-test `PASS/FAIL` and totals.

## Brief Architecture Overview
- `DocService`, `PatientService`, `AppService` manage core records.
- `ScheduleService` validates doctor availability before appointment creation.
- `BillService` computes and stores bills.
- `BillSummaryService` builds read-friendly bill output.
- `RecommendationService` maps symptom keywords to specialization and suggests doctors.
- `CsvStore` is the shared utility for CSV read/write.

## Persistence
- CSV files are created under `data/`.
- Services load persisted rows in constructors.
- Mutating operations save updated lists/maps back to CSV.

