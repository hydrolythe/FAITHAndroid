$pngs = Get-ChildItem -Recurse -Path "..\app\src\main\res\drawable*" | where { $_.extension -eq ".png" }
foreach  ($png in $pngs) {
 .\cwebp.exe -lossless -mt $png -o "$(Join-Path $png.DirectoryName $png.BaseName).webp"
}
foreach  ($png in $pngs) {
	Remove-Item $png
}