#!/bin/sh
java -cp ./bin:\
lib/gnu.getopt-1.0.9.jar:\
lib/ivy-java-1.2.6.jar:\
lib/jakarta-regexp-1.5.jar:\
lib/libTUIO.jar:\
lib/commons-math3-3.2.jar:\
lib/JMapViewer.jar:\
lib/Serializable_jcoord-1.0.jar:\
lib/vlcj-3.0.0.jar:\
lib/jna-4.1.0.jar \
-Xms512m -Xmx1024m \
com.deev.interaction.uav3i.ui.Launcher
