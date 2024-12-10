
all: 
	javac -d ./bin/ -cp src/:../_lib/paranamer-2.8.jar:../libs/imgscalr-lib-4.2.jar:../libs/opencv-2.4.9-7.jar:../libs/javax.mail.jar:../_lib/Paulscode\ 3D\ sound/SoundSystem.jar:../_lib/Paulscode\ 3D\ sound/CodecJOgg.jar:../_lib/Paulscode\ 3D\ sound/CodecJOrbis.jar:../_lib/Paulscode\ 3D\ sound/CodecWav.jar:../_lib/Paulscode\ 3D\ sound/SoundSystemJPCT.jar:../libs/commons-io-2.8.0/commons-io-2.8.0.jar -g src/com/zandgall/arvopia/ArvopiaLauncher.java
