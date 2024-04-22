Preparations

1.start axon server 

If you have Docker, you can run this commands

"docker run --name axonserver -p 8024:8024 -p 8124:8124 -e axoniq.axonserver.standalone=true -v "C:\Axon-Config\data":/axonserver/data -v "C:\Axon-Config\eventdata":/axonserver/eventdata -v "C:\Axon-Config\config":/axonserver/config axoniq/axonserver:2023.2.3-jdk-17"

