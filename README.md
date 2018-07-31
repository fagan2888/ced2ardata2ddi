[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1186911.svg)](https://doi.org/10.5281/zenodo.1186911)

# ced2ardata2ddi
This project contains java classes that will allow you to create a REST service.  The web service takes a message, with an attached (Stata or SPSS) dataset, and returns an xml response that is in DDI-C codebook format.

### Build

1. Clone the github repository to your machine.
2. Go to the root directory of the cloned repository.
3. Use maven 2 to build the project. On the command line, enter the following command

   ```mvn clean install```  

### Usage 

The best way to use this SOA service is to:
1. Look at code that sends a _request_ to the service end point (http://.../ced2ardata2ddi/data2ddi). See the _convertData()_ method in [ced2ar/Source/src/main/java/edu/ncrn/cornell/ced2ar/ei/controllers/Upload.java](https://github.com/ncrncornell/ced2ar/blob/master/Source/src/main/java/edu/ncrn/cornell/ced2ar/ei/controllers/Upload.java)
2. Look at the code that receives the request and returns the reposone.  See the _spss2DDI()_ method in [ced2ardata2ddi/src/main/java/edu/cornell/ncrn/ced2ar/ced2ardata2ddi/web/controllers/DataFileRestController.java](https://github.com/ncrncornell/ced2ardata2ddi/blob/master/src/main/java/edu/cornell/ncrn/ced2ar/ced2ardata2ddi/web/controllers/DataFileRestController.java)

This war file depends on: [ced2arddigenerator](https://github.com/ncrncornell/ced2arddigenerator) to do the actual dataset conversions.
