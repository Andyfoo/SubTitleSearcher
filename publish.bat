md _publish
md _publish\SubTitleSearcher
_release\copy_files.exe target\SubTitleSearcher.exe _release\SubTitleSearcher\
_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher\
_release\copy_files.exe _release\SubTitleSearcher\bin _publish\SubTitleSearcher\bin
_release\copy_files.exe _release\SubTitleSearcher\jre _publish\SubTitleSearcher\jre
cd _publish\
..\_release\zip.exe -r SubTitleSearcher×ÖÄ»ÏÂÔØ.zip SubTitleSearcher
