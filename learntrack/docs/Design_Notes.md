# Design Notes

## Why ArrayList instead of a plain array?

Every collection in this project (students, courses, enrollments, and a student's completed courses) grows at runtime as the admin adds records through the menu — the final size isn't known up front. A plain array (`Student[]`) has a fixed length chosen at creation time, so adding one more student than the array's capacity would mean manually allocating a bigger array and copying every element over. `ArrayList` does exactly that resizing internally, and on top of that gives useful built-in operations (`add`, `remove`, iteration with a `for-each` loop, `isEmpty()`) instead of us hand-rolling them. Since this is an in-memory, admin-driven console app with no predictable upper bound on records, `ArrayList` is the natural fit.

## Where static members are used, and why

- **`util.IdGenerator`** — `studentIdCounter`, `courseIdCounter`, and `enrollmentIdCounter` are `private static int` fields, each shared across the entire application rather than duplicated per object. Because they're static, every call to `getNextStudentId()` (a `static` method) increments the *same* counter, guaranteeing every student gets a unique, auto-generated ID instead of the admin having to invent one by hand (and risk two students colliding on the same ID).
- **`util.InputValidator`** — its helper methods (`requireNonBlank`, `requirePositive`, `isValidEmail`) don't need any per-instance state, so they're `static` too. There's no reason to create an `InputValidator` object just to call a stateless check.

Both utility classes have a private constructor to make clear they're not meant to be instantiated — they exist purely to group related static helpers.

## Where inheritance is used, and what it bought us

`Person` is the base class for `Student` and `Trainer`, holding the fields every person in the system has in common: `id`, `firstName`, `lastName`, `email`. Both subclasses call `super(...)` in their constructors to let `Person` set up those shared fields, so that logic is written once instead of copy-pasted into both subclasses.

`Person` also defines `getDisplayName()`, and `Student`/`Trainer` each `@Override` it to add role-specific context (`"Student: ... (Batch ...)"` vs. `"Trainer: ..."`). This is the polymorphism payoff: calling `person.getDisplayName()` produces different output depending on the *actual* runtime type of `person`, without the calling code needing an `if (person instanceof Student)` check anywhere.

The practical gain: if another `Person` subtype needs to be added in the future, it only needs to implement what's different about it — the shared identity fields and behavior are already there for free.
