# IBM Data Merge Utility v4.0.0

# TODO List

- jUnit Test Driven Refactor 6 - Performance Optimization - Content Classes
 - Content
  - Segment
  - TextSegment
  - TagSegment
  - BookmarkSegment
  - Content
 - Data classes
  - DataElement
  - DataPrimitive
  - DataObject
  - DataList
 - Path & PathPart 
 - DataManager  
 - Source / Provider Pairs
  - Abstract
  - Stub
  - Cache
  - FileSystem
  - Rest
  - Jdbc
  - Cloudant
  - Mongo
 - Config
 - Template / TemplateList 
 - TemplateCache
 - Merger
 - Directives
  - Require
  - Replace
  - Insert 
  - ArchiveJar
  - SaveFile
 - Enrichment
  - XmlSaxHandlerData
  - XmlSaxHandlerMarkup
  - ParseData
  - Enrich Directive

- JavaDoc for everything

- Swagger

- Functional Testing Preparations
 - Setup Local Liberty : 2hrs
 - Setup Local Cloudant (Docker) : 2hrs
 - Setup Local Mongo : 2hrs
 - Setup HTTP resources for Rest provider testing : 1hr
 - Perl Script to run tests : 2hrs
- Functional Testing : 120hrs
 - Simple parameter replace (json to xml & back)
 - Simple File/Html Provider of Data sources for Tests
  - Sub-Template Iteration Safety
  - Archive Generation
 - Full-on Provider Test (per provider)
  - Multi-Data Source layered enrichment
  - JPMC Config Generation templates?
  - Complex Transform 
   - IDMU v3 -> v4 Templates
        
- Performance Test Suite - 40hrs

# Web Development
    - Maven for Angular / Bootstrap
     - angular-ui-bootstrap 
        "https://mvnrepository.com/artifact/org.webjars/angular-ui-bootstrap"
    - Documentation WebApp - 40hrs
    - Documentation Data - 20hrs
    - Template Editor WebApp - 60hrs

# Enhancements Deferred to future iteration
    - Configuration Editor
    - PutConfig - Temporary Update of Sources
    - PostConfig - use BlueMix API to set env var & restage
        cf set-env my-app my-var-name my-var-value (Cmd Line);
    - Template Api Explorer
    - Refactor core to utility jar packaging
    - JMS Provider (MQ provider?)
    - Optimize ReplaceProcess - Single pass string biulder replaceall(HashMap<String,String>)
    - Robust Rest provider (support for common authentication schemes?)
