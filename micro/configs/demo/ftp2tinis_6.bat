REM Copy the Node.tini file to each of the TINI boards on Robot 6.

REM ftp -v -n -i 192.168.6.1 < ftpcommands.txt
ftp -v -n -i 192.168.6.2 < ftpcommands.txt
ftp -v -n -i 192.168.6.3 < ftpcommands.txt
ftp -v -n -i 192.168.6.4 < ftpcommands.txt
ftp -v -n -i 192.168.6.5 < ftpcommands.txt
ftp -v -n -i 192.168.6.6 < ftpcommands.txt

