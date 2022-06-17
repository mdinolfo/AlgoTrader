
#
#  Makefile
#
#  Copyright (C) 2022 Michael Dinolfo
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

JFLAGS = -cp src:lib/*
JC = javac
MELSRC = src/AlgoTrader
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

MELCLASSES = \
	$(MELSRC)/Configuration.java \
	$(MELSRC)/Logger.java \
	$(MELSRC)/Order.java \
	$(MELSRC)/PriceSocket.java \
	$(MELSRC)/OrderSocket.java \
	$(MELSRC)/Trader.java \
	$(MELSRC)/Main.java \

AlgoTrader: \
	$(MELCLASSES:.java=.class)
	cd src; \
	jar cfm AlgoTrader.jar MANIFEST.MF AlgoTrader/; \
	mv AlgoTrader.jar ../bin

default: AlgoTrader

clean:
	$(RM) $(MELSRC)/*.class
	$(RM) bin/AlgoTrader.jar
