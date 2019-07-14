rd /s /q _publish\SubTitleSearcher
md _publish
md _publish\SubTitleSearcher
del /q _publish\SubTitleSearcher×ÖÄ»ÏÂÔØ.zip

_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher\
_release\copy_files.exe _release\bin _publish\SubTitleSearcher\bin
_release\copy_files.exe _publish\jre _publish\SubTitleSearcher\jre

cd _publish
..\_release\zip.exe -r  SubTitleSearcher×ÖÄ»ÏÂÔØ.zip SubTitleSearcher
cd..
