_release\copy_files.exe target\SubTitleSearcher.exe _release\SubTitleSearcher\

_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher_jre8\
_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher\


cd _publish\
..\_release\zip.exe -r SubTitleSearcher��Ļ����(jre8).zip SubTitleSearcher_jre8
..\_release\zip.exe -r SubTitleSearcher��Ļ����.zip SubTitleSearcher
