$pngs = Get-ChildItem -Recurse -Path "C:\Users\FAITH\AndroidStudioProjects\FAITH\app\src\main\res\drawable*" | where {$_.extension -eq ".png"}
foreach  ($png in $pngs) {
 .\cwebp.exe -lossless -mt $png -o "$(Join-Path $png.DirectoryName $png.BaseName).webp"
}
foreach  ($png in $pngs) {
	Remove-Item $png
}