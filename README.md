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

# Программа для сканирования каталогов
### Задача
- Написать программу, которая ищет файлы в каталогах, полные пути к которым перечисляются в параметрах программы. Каталоги могут быть как сетевыми, так и нет.

### ВЫВОД
- Итоговым выводом является файл в кодировке UTF-8, в котором перечислены все найденные файлы.
- Допустим, итоговый файл выглядит так (формат вывода должен быть именно таким как в этом примере):

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

Чтобы было понятно как делать нельзя, приводим разъяснения по целевому способу использования программы.

Итак, мы просканировали нашей программой все каталоги в первый раз. Далее, например, через неделю просканировали второй раз. Взяли полученные 2 файла. Сравнили их, например, с помощью WinMerge. В результате увидели:
- что добавилось,
- что убавилось,
- что изменилось.

Таким образом, если вывод от запуска к запуску будет неупорядоченным, пользы от программы не будет.

Для того, чтобы не было скучно глядеть в пустую консоль, нужно сопроводить процесс сканирования выводом через каждый 6 секунд «точки» (.), а через каждую минуту палочки (|). Если вы хотите что-то «сказать» в консоль, делать это надо на английском языке.

Ниже пример параметров, при которых ничего не сканируется:
```text
"\\epbyminsd0235\Video Materials" "\\EPUALVISA0002.kyiv.com\Workflow\ORG\Employees\Special"
 "\\EPUALVISA0002.kyiv.com\Workflow\ORG\Employees\Lviv" - "\\epbyminsd0235\Video Materials"
  "\\EPUALVISA0002.kyiv.com\Workflow\ORG\Employees\Special" "\\EPUALVISA0002.kyiv.com\Workflow\ORG\Employees\Lviv"
```

Должны работать разные варианты входных параметров (то есть, не должно быть такого, что программа протестирована только для одного каталога на диске C:\ c одним исключением).

Должно быть учтено возможное расширение набора ключей (помимо «минуса»). Например, чтобы исключить из вывода ряд файлов (таких как Thumbs.db).

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ
- Функциональность программы должна быть покрыта JUnit-тестами. Должна быть обеспечена возможность запуска всех тестов сразу.
- Для ускорения работы программы разработчик должен использовать многопоточность. На умение работать с потоками будет обращено особое внимание.
- Файлов во всех каталогах может быть несколько миллионов.
- Программа должна работать быстро и с экономным потреблением оперативной памяти.
- Любой выбор структур данных, подходов и алгоритмов должен быть лаконично и емко обоснован комментарием. Например, если был сделан выбор в пользу ArrayList (150000), нужно пояснение почему. Если размер файлового буфера выбран равным 8192, то почему и т.д. Из кода и комментариев в нем (на русском языке) должен быть ясен ход мысли автора. Мы должны видеть, что выбор сделан с пониманием, а не случайно.
- Ход программы, ее алгоритм, ее циклы должны быть простыми и без замысловатостей, чтобы не приходилось тратить нервные клетки мозга на понимание.
- Должна использоваться Java версии 6 или больше.
- Использовать сторонние программы и библиотеки нельзя. Только Java SE. Это, конечно, не касается JUnit- тестов 
- Все другие вопросы по особенностям реализации приложения решаются разработчиком самостоятельно, эти решения отражают знания и опыт разработчика и учитываются при оценке созданного продукта.
- Приложение должно решать поставленные задачи, выполнение дополнительных функций, не приведенных в задании, не влияет на оценку решения (за редким исключением).
- При оценке решения учитываются следующие факторы:
  - применение принципов ООП, возможность расширения приложения и повторного использования кода,
  - простота и читаемость кода (в том числе комментирование),
  - архитектура решения (способы работы с данными),
  - структура кода. 
- Готовое решение должно представлять из себя архив, в котором находятся исходные тексты программы и JUnit-тестов.