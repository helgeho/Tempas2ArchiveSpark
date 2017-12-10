# [*Tempas*](http://tempas.l3s.de) to [ArchiveSpark](https://github.com/helgeho/ArchiveSpark)

This project contains the required components for [ArchiveSpark](https://github.com/helgeho/ArchiveSpark) to enter a Web archive (the [Internet Archive](http://archive.org)'s [Wayback Machine](http://archive.org/web)) through temporal search results provided by [*Tempas*](http://tempas.l3s.de) (v2).

It includes an ArchiveSpark *DataSpec*, named `TempasWaybackSpec`, which can be configured using the following parameters:
* ***query*** (String): The query keywords.
* ***from*** (Int, optional): The begin year of the query time interval.
* ***to*** (Int, optional): The begin year of the query time interval.
* ***pages*** (Int, optional): How many pages to receive, i.e., the level of parallelism.
* ***resultsPerPage*** (Int, optional): How many results per page.

E.g., `TempasWaybackSpec("obama", from = 2005, to = 2012, pages = 10, resultsPerPage = 50)`:
```scala
val rdd = ArchiveSpark.load(sc, TempasWaybackSpec("obama", from = 2005, to = 2012, pages = 10, resultsPerPage = 50))
```

An example is provided as [Jupyter](http://jupyter.org/) notebook under: [*CompareMenus-DMvsEuro*](examples/CompareMenus-DMvsEuro.ipynb).
In this toy study we compare restaurant prices and find that they actually increased when the Euro was introduced in Germany.

## Usage

To use this library, we recommend an interactive environment, such as [*Jupyter*](http://jupyter.org/) in combination with [*Toree*](https://toree.apache.org/*) to run [*Spark*](http://spark.apache.org/) instructions on a cluster.

Please make sure that you have both [ArchiveSpark](https://github.com/helgeho/ArchiveSpark) as well as this *Tempas2ArchiveSpark* library in your classpath. 

To build a JAR file of this project, we recommend to use [*SBT*](http://www.scala-sbt.org/):
```
git clone https://github.com/helgeho/Tempas2ArchiveSpark.git
cd Tempas2ArchiveSpark
sbt assembly
```

Now the resulting JAR file should be located under `Tempas2ArchiveSpark/target/tempas-archivespark-assembly-1.0.0.jar`.