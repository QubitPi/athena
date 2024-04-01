---
layout: doc-default
---

<div class="text-light background-dark">
  <div class="jumbotron">
    <div class="container text-center">
      <h2>
        File <span class="text-primary">{json:api}</span> & Meta Data <span class="text-primary">GraphQL</span> web
        services for file management
      </h2>
      <a href="pages/guide/v1/01-welcome.html">
        <button type="button" class="btn btn-primary text-light my-3">Get Started</button>
      </a>
      <div class="mb-3">
        <img src="assets/img/athena-illustration.png" class="img-fluid athena-illustration" alt="Athena Illustration">
      </div>
    </div>
  </div>
</div>

<div class="container text-center my-5">
  <img src="assets/img/athena-logo.png" class="img-fluid mb-3" alt="Athena Logo" width="20%">
  <h4 class="mx-2">Athena is a Java library that lets you setup object storage webservice with minimal effort.</h4>
</div>

<hr class="mx-5">

<div class="usage container my-3">
  <div class="text-center display-4 mb-2">How to use it</div>
  <div class="tab-content">
    <div role="tabpanel" class="tab-pane active">
      <div class="feature row align-items-center my-4">
        <div class="col-sm">
          <div class="mr-3">
            <h4>1. Star Athena 😉</h4>
          </div>
        </div>
      </div>
      <div class="feature row align-items-center my-4">
        <div class="col-sm">
          <div class="mr-3">
            <h4>2. Pickup a File Store</h4>
            <p>
              Choose either Swift or HDFS file store that Athena already offered for you or implement a custom file
              store against any arbitrary database as file storage
            </p>
          </div>
        </div>
        <div class="col-sm">
          <img src="assets/img/editor/filestore.png" class="img-fluid" alt="Editor: File Store">
        </div>
      </div>
      <div class="feature row align-items-center my-4">
        <div class="col-sm">
          <div class="mr-3">
            <h4>3. Define GraphQL Data Fetchers</h4>
            <p>Plug-in access layer to file meta data through GraphQL Java</p>
          </div>
        </div>
        <div class="col-sm">
          <img src="assets/img/editor/quey-datafetcher.png" class="img-fluid" alt="Editor: DataFether">
        </div>
      </div>
      <div class="feature row align-items-center my-4">
        <div class="col-sm">
          <div class="mr-3">
            <h4>4. Bind Them</h4>
            <p>Make instances of your file store and data fetchers accessible through Jersey binding.</p>
          </div>
        </div>
        <div class="col-sm">
          <img src="assets/img/editor/jersey-bind.png" class="img-fluid" alt="Editor: Jersey Bind">
        </div>
      </div>
      <div class="feature row align-items-center my-4">
        <div class="col-sm">
          <div class="mr-3">
            <h4>5. Deploy & Enjoy</h4>
            <p>That's it - you are ready to deploy and query your file data with JSON or GraphQL requests.</p>
          </div>
        </div>
        <div class="col-sm">
          <img src="assets/images/editor/crud/query.png" class="img-fluid" alt="Query">
        </div>
      </div>
    </div>
  </div>
  <div class="text-center mt-5">
    <h2>Want to learn more?</h2>
    <a href="pages/guide/v1/01-welcome.html">
      <button type="button" class="btn btn-primary text-light mb-2">Documentation</button>
    </a>
    <p>Or see our features below</p>
  </div>
</div>

<div class="text-light background-dark">
  <div class="container py-5">
    <div class="text-center display-4 mb-5">Features</div>
    <div class="feature row align-items-center">
      <div class="col-sm text-center text-sm-right d-block d-sm-none text-sm-center">
        <img src="assets/images/features/code-icon.png" class="img-fluid" alt="Features: Production Quality">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Production Quality</h4>
        <p>
          Quickly build and deploy production quality web services that expose your data as a service. Athena APIs
          support complex filtering rules, sorting, and pagination.
        </p>
      </div>
      <div class="col-sm text-right d-none d-sm-block">
        <img src="assets/images/features/code-icon.png" class="img-fluid" alt="Features: Production Quality">
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-right"></div>
      <div class="elbow-center"></div>
      <div class="elbow-left"></div>
    </div>
    <div class="feature row align-items-center">
      <div class="col-sm">
        <img src="assets/images/features/secure-icon.png" class="img-fluid" alt="Features: Security Comes Standard">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Security Comes Standard</h4>
        <p>Controlling access to your data is as simple as defining your rules.</p>
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-left"></div>
      <div class="elbow-center"></div>
      <div class="elbow-right"></div>
    </div>
    <div class="feature row align-items-center">
      <div class="col-sm text-center text-sm-right d-block d-sm-none">
        <img src="assets/images/features/mobile-icon.png" class="img-fluid" alt="Features: Mobile Friendly">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Mobile Friendly</h4>
        <p>
          Fetch entire object graphs in a single round trip. Subscribe to changes in your data model in realtime. Only
          requested elements of the file meta data are returned.
        </p>
      </div>
      <div class="col-sm text-right d-none d-sm-block">
        <img src="assets/images/features/mobile-icon.png" class="img-fluid" alt="Features: Mobile Friendly">
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-right"></div>
      <div class="elbow-center"></div>
      <div class="elbow-left"></div>
    </div>
    <div class="feature row align-items-center">
      <div class="col-sm">
        <img src="assets/images/features/atom-icon.png" class="img-fluid" alt="Features: Single Atomic Request">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Single Atomic Request</h4>
        <p>Athena supports multiple data model mutations in a single request in GraphQL.</p>
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-left"></div>
      <div class="elbow-center"></div>
      <div class="elbow-right"></div>
    </div>

    <div class="feature row align-items-center">
      <div class="col-sm text-center text-sm-right d-block d-sm-none">
        <img src="assets/images/features/analytics-icon.png" class="img-fluid" alt="Features: Analytic Queries">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Analytic Queries</h4>
        <p>
          Athena supports analytic queries against files crafted with its powerful semantic layer. Athena API's work
          natively with <a href="https://github.com/yahoo/fili">Fili</a> to visualize, explore, and report on your
          data.
        </p>
      </div>
      <div class="col-sm text-right d-none d-sm-block">
        <img src="assets/images/features/analytics-icon.png" class="img-fluid" alt="Features: Analytic Queries">
      </div>
    </div>

    <div class="feature row align-items-center elbow">
      <div class="elbow-right"></div>
      <div class="elbow-center"></div>
      <div class="elbow-left"></div>
    </div>
    <div class="feature row align-items-center">
      <div class="col-sm">
        <img src="assets/images/features/globe-icon.png" class="img-fluid" alt="Features: Open API">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Open API</h4>
        <p>
          Explore, understand, and compose queries against your Athena API through generated <a>Swagger</a>
          documentation or GraphQL schema.
        </p>
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-left"></div>
      <div class="elbow-center"></div>
      <div class="elbow-right"></div>
    </div>
    <div class="feature row align-items-center">
      <div class="col-sm text-center text-sm-right d-block d-sm-none">
        <img src="assets/images/features/customize-icon.png" class="img-fluid" alt="Features: Customize">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Customize</h4>
        <p>Customize the behavior of file and meta data operations with pluggable implementations through Jersey.</p>
      </div>
      <div class="col-sm text-right d-none d-sm-block">
        <img src="assets/images/features/customize-icon.png" class="img-fluid" alt="Features: Customize">
      </div>
    </div>
    <div class="feature row align-items-center elbow">
      <div class="elbow-right"></div>
      <div class="elbow-center"></div>
      <div class="elbow-left"></div>
    </div>

    <div class="feature row align-items-center">
      <div class="col-sm">
        <img src="assets/images/features/annotation-icon.png" class="img-fluid" alt="Features: Storage Agnostic">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Storage Agnostic</h4>
        <p>
          Athena is agnostic to your particular persistence strategy. Use Swift/HDFS or provide your own implementation
          of a data store.
        </p>
      </div>
    </div>

    <div class="feature row align-items-center elbow">
      <div class="elbow-left"></div>
      <div class="elbow-center"></div>
      <div class="elbow-right"></div>
    </div>

    <div class="feature row align-items-center">
      <div class="col-sm text-center text-sm-right d-block d-sm-none">
        <img src="assets/images/features/open-source-icon.png" class="img-fluid" alt="Features: Open Source">
      </div>
      <div class="col-sm my-3 my-sm-0">
        <h4>Open Source</h4>
        <p>
          Athena is 100% open source and available on <a href="https://github.com/paiondata/athena">Github</a>.
          Released under the commercial-friendly <a href="pages/license.html">Apache License, Version 2.0</a>.
        </p>
      </div>
      <div class="col-sm text-right d-none d-sm-block">
        <img src="assets/images/features/open-source-icon.png" class="img-fluid" alt="Features: Open Source">
      </div>
    </div>

  </div>
</div>

<div class="container text-center my-5">
  <h2>Opinionated API's for web & mobile</h2>
  <p>Improve the velocity and quality of your team's work.</p>
  <a href="pages/guide/v1/01-welcome.html">
    <button type="button" class="btn btn-primary text-light">Get Started</button>
  </a>
</div>

<div class="footer text-light background-dark">
  <div class="container py-3">
    <div class="row">
      <div class="col-sm">
        <img src="assets/img/athena-white-logo.png" class="img-fluid" alt="Athena Logo" width="30%">
      </div>
      <div class="col-sm links">
        <a href="pages/guide/v1/01-welcome.html">Documentation</a>
        <a href="pages/license.html">Licensing</a>
      </div>
      <div class="col-sm links">
        <a href="https://discord.com/widget?id=1001320502960324658&theme=dark">Community</a>
      </div>
      <div class="col-sm links">
        <a href="https://github.com/QubitPi?tab=packages&repo_name=athena">Releases</a>
      </div>
      <div class="col-sm">
        <a href="https://github.com/paiondata/athena">
          <button type="button" class="btn btn-secondary github">
            <img src="assets/images/GitHub-Mark-Light-32px.png" class="img-fluid" alt="Github Logo">
            <span>Github</span>
          </button>
        </a>
      </div>
    </div>
  </div>
</div>
