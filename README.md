+ tdlib папка БД TDLib, ее расположение можно настроить в конфиге
+ td сама библиотека TDLib, скомпилирована на win64
+ org.drinkless.tdlib - служебные классы скомпилированные баблиотекой tdlib

TDLib получается слишком большой поэтому ее нет в репозитории. Плюс 
некоторые файлы больше 100MB поэтому при пуше и пуле 
надо юзать git lfs. https://git-lfs.com/ Но этот инструмент перекодирует 
файлы, что ухудшает читаемость проекта  
Обращаю внимание что скорее всего надо будет 
юзать команду ```git lfs migrate ``` [подробнее тут](https://github.com/git-lfs/git-lfs/blob/main/docs/man/git-lfs-migrate.adoc?utm_source=gitlfs_site&utm_medium=doc_man_migrate_link&utm_campaign=gitlfs).

Перед запуском нужно настроить переменные среды, а также путь до папки td.
```-Djava.library.path=<path>\td\tdlib\bin```

