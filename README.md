# Directory Scanner

Searches for files in catalogs received as absolute paths in command line arguments. 
Catalogs may be reachable via network.

## Output
File with UTF-8 encoding, that contains all found files in the catalogs.

Example output:
```text
[
file = \\epbyminsd0235\Video Materials\.DS_Store
date = 2011.07.20
size = 6148][
file = \\epbyminsd0235\Video Materials\2008.ivc
date = 2008.12.12
size = 415892][
file = \\epbyminsd0235\Video Materials\CDP DAM.ivc
date = 2009.01.29
size = 3207246][
file = \\epbyminsd0235\Video Materials\.NET Mentoring Program\Acceptance Testing Through UI\2010-01-19 10.13 Acceptance Testing.wmv date = 2010.01.19
size = 22904839][
file = \\epbyminsd0235\Video Materials\.NET Mentoring Program\Acceptance Testing Through UI\2010-01-19 10.50 Acceptance Testing.wmv date = 2010.01.19
size = 106224657]
```

The output is alphabetically ordered, so that it can be reused from run to run of the app.

## Input
Besides absolute paths to catalogs, the user can provide catalogs to be excluded from scanning,
to do this, use '-' key followed by space separated list of absolute paths to catalogs.

The program can handle Windows and Unix paths as input.

## Concurrency
This version of the app uses only Java APIs for concurrency:
- ForkJoinPool
- CompletableFuture
- Thread
- ScheduledExecutorService
- RecursiveAction
- LinkedBlockingQueue


