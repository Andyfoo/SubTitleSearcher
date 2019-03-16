md _publish
md _publish\SubTitleSearcher
md _publish\SubTitleSearcher_jre8
_release\copy_files.exe target\SubTitleSearcher.exe _release\SubTitleSearcher\

_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher_jre8\
_release\copy_files.exe _release\SubTitleSearcher\bin _publish\SubTitleSearcher_jre8\bin
_release\copy_files.exe _release\SubTitleSearcher\jre _publish\SubTitleSearcher_jre8\jre


_release\copy_files.exe target\SubTitleSearcher.exe _publish\SubTitleSearcher\
_release\copy_files.exe _release\SubTitleSearcher\bin _publish\SubTitleSearcher\bin
_release\copy_files.exe _release\jre-11 _publish\SubTitleSearcher\jre


cd _publish\
..\_release\zip.exe -r SubTitleSearcher×ÖÄ»ÏÂÔØ(jre8).zip SubTitleSearcher_jre8
..\_release\zip.exe -r SubTitleSearcher×ÖÄ»ÏÂÔØ.zip SubTitleSearcher
