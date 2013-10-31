################################################################
# 
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#  
#    http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#  
################################################################


library(TTR)

data<-read.csv("75m.csv")
data$date<-as.Date(data$date)

#plot daily downloads
plot(data$count_total/1000~data$date,ylim=range(0,300),xlim=c(as.Date("2012-01-01"),as.Date("2014-01-01")),
ylab="Thousands of Downloads",xlab="Date",main="Daily Downloads")
lines(data$date,SMA(data$count_total/1000,7),col="red",lwd=5)

abline(v=as.Date("2012-05-08"))
text(as.Date("2012-05-08"),25,"3.4.0",pos=4,cex=0.5)

abline(v=as.Date("2012-08-23"))
text(as.Date("2012-08-23"),25,"3.4.1",pos=4,cex=0.5)

abline(v=as.Date("2013-07-23"))
text(as.Date("2013-07-23"),25,"4.0.0",pos=4,cex=0.5)

abline(v=as.Date("2013-10-01"))
text(as.Date("2013-10-01"),25,"4.0.1",pos=4,cex=0.5)


#Linux 64-bit
plot(data$date,data$linux64/(data$linux64+data$linux32),xlab="Date",ylab="64-bit downloads as fraction of all Linux downloads",main="64-bit Download Trend")
lines(data$date,SMA(data$linux64/(data$linux64+data$linux32),7),col="red",lwd=5)

abline(v=as.Date("2012-05-08"))
text(as.Date("2012-05-08"),0.7,"3.4.0",pos=4,cex=0.5)

abline(v=as.Date("2012-08-23"))
text(as.Date("2012-08-23"),0.7,"3.4.1",pos=4,cex=0.5)

abline(v=as.Date("2013-07-23"))
text(as.Date("2013-07-23"),0.7,"4.0.0",pos=4,cex=0.5)

abline(v=as.Date("2013-10-01"))
text(as.Date("2013-10-01"),0.7,"4.0.1",pos=4,cex=0.5)


#RPM Versus DEB
plot(data$date,data$rpm/data$deb,
xlab="Date",
ylab="RPM/DEB Ratio",
main="Linux Packaging Trend")

lines(data$date,SMA(data$rpm/data$deb,7),col="red",lwd=5)

abline(v=as.Date("2012-05-08"))
text(as.Date("2012-05-08"),2.5,"3.4.0",pos=4,cex=0.5)

abline(v=as.Date("2012-08-23"))
text(as.Date("2012-08-23"),2.5,"3.4.1",pos=4,cex=0.5)

abline(v=as.Date("2013-07-23"))
text(as.Date("2013-07-23"),2.5,"4.0.0",pos=4,cex=0.5)

abline(v=as.Date("2013-10-01"))
text(as.Date("2013-10-01"),2.5,"4.0.1",pos=4,cex=0.5)


#OS Trends

x_range <- range(data$date[1],data$date[length(data$date)])
y_range <- range(min(data$windows, data$mac, data$linux)/1000, max(data$windows, data$mac, data$linux)/1000)

plot(log="y",data$date,data$windows/1000,ylim=y_range,xlim=x_range,
xlab="Date",ylab="Daily Downloads (Thousands)",type="n", main="Trend in OS")

points(data$date,data$windows/1000,pch=0,col="black")
points(data$date,data$mac/1000,pch=1,col="red")
points(data$date,data$linux/1000,pch=2,col="blue")

legend(as.numeric(data$date)[1]+7, 55, c("Windows","Mac","Linux"), cex=0.8, col=c("black","red","blue"), pch=0:2)

abline(v=as.Date("2012-05-08"))
text(as.Date("2012-05-08"),210,"3.4.0",pos=4,cex=0.5)

abline(v=as.Date("2012-08-23"))
text(as.Date("2012-08-23"),210,"3.4.1",pos=4,cex=0.5)

abline(v=as.Date("2013-07-23"))
text(as.Date("2013-07-23"),210,"4.0.0",pos=4,cex=0.5)

abline(v=as.Date("2013-10-01"))
text(as.Date("2013-10-01"),210,"4.0.1",pos=4,cex=0.5)


# Language totals

dotchart(rev(c(
sum(data$ar),
sum(data$ast),
sum(data$cs),
sum(data$da),
sum(data$de),
sum(data$el),
sum(data$en_GB),
sum(data$en_US),
sum(data$es),
sum(data$eu),
sum(data$fi),
sum(data$fr),
sum(data$gd),
sum(data$gl),
sum(data$hu),
sum(data$it),
sum(data$ja),
sum(data$km),
sum(data$ko),
sum(data$lt),
sum(data$nb),
sum(data$nl),
sum(data$pl),
sum(data$pt),
sum(data$pt_BR),
sum(data$ru),
sum(data$sk),
sum(data$sl),
sum(data$sr),
sum(data$sv),
sum(data$ta),
sum(data$tr),
sum(data$vi),
sum(data$zh_TW),
sum(data$zh_CN)
))/1000,
labels=rev(c("ar","ast","cs","da","de","el","en_GB","en_US","es","eu","fi","fr","gd","gl","hu","it","ja","km","ko","lt","nb","nl","pl","pt","pt_BR","ru","sk","sl","sr","sv","ta","tr","vi","zh_CN","zh_TW")),
cex=0.8,xlab="Thousands of Downloads",main="Downloads by Language")

