<!DOCTYPE html>

<head>
  <meta charset="utf-8">
  <title>${title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" lang="en">
  <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
  <link rel="stylesheet" href="css/normalize.css">
  <link rel="stylesheet" href="css/html5bp.css">
  <link rel="stylesheet" href="css/main.css">
</head>

<body>
  <div id="title">
    <br>
    <h1> Stars </h1>
    <canvas id="mapCanvas"></canvas>
  </div>
  <div>
    <!-- Again, we're serving up the unminified source for clarity. -->
    <script src="js/jquery-2.1.1.js"></script>
  </div>
  <div class="column">
    <div class="sub-head">
      <h1 class="sub-head"> Search for neighbors </h1>
    </div>
    ${neighbors}
  </div>
  <div class="column">
    <div class="sub-head">
      <h1> Load Data </h1>
    </div>
    <div style="color: white;">
      <p class="results" style="color: white;"> ${stars}</p>
    </div>
    <div class="sub-head">
      <h1>Results</h1>
    </div>
    <div style="color: black;">
      <p class="results" id="box">${results}</p>
    </div>
  </div>
  <div class="column">
    <div class="sub-head">
      <h1> Search within radius </h1>
    </div>
    ${radius}
  </div>


</body>
<!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->

</html>
