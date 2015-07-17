var gulp     = require("gulp");
var gutil    = require("gulp-util");
var concat   = require("gulp-concat");
var filesize = require("gulp-filesize");
var uglify   = require("gulp-uglify");
var gulpif   = require("gulp-if");
var rename   = require("gulp-rename");
var insert   = require("gulp-insert");
var del      = require("del");
var reload   = require("gulp-livereload");
var nodemon  = require("gulp-nodemon");

var _buildDir = "../resources/META-INF/resources/editor";

var paths = {

  /* HTML source files */
  html: ["src/html/app.html"],

  /* CSS source files */
  css: ["src/css/app.css"],
  lib_css: ["bower_components/bootstrap/dist/css/bootstrap.min.css"],

  /* Fonts source files */
  font: ["src/font/**"],

  /* Images files */
  image: "src/image/**",

  /* JS source files */
  js: ["src/js/i18n/i18n.js",
       "src/js/components/*.react.js",
       "src/js/modules/*.js",
       "src/js/app.js"],

  /* I18N files */
  i18n_default: {
    "en": ["src/js/i18n/en.js"]
  },
  i18n: {
    "en": ["src/js/i18n/en.js"]/*,
    "fr": ["src/js/i18n/fr.js",
           "bower_components/moment/locale/fr.js",
           "bower_components/moment/numeraljs/min/languages/fr.min.js"]*/
  },

  /* Dependencies source files */
  /* Must be minified here*/
  lib_js: ["bower_components/jquery/dist/jquery.min.js",
           "bower_components/bootstrap/dist/js/bootstrap.min.js",
           "bower_components/moment/min/moment.min.js",
           "bower_components/numeraljs/min/numeral.min.js",
           "bower_components/director/build/director.min.js",
           "bower_components/react/react-with-addons.min.js",
           "bower_components/Sortable/Sortable.min.js",
           "bower_components/Sortable/react-sortable-mixin.js"
          ],
  config: {
    'dev': ["src/js/config/dev.js"],
    'itg': ["src/js/config/itg.js"],
    'prod': ["src/js/config/prod.js"]
  },

  /* File destination folder */
  js_dest: "build/js",
  lib_dest: "build/lib",
  html_dest: "build/html",
  css_dest: "build/css",
  font_dest: "build/font",
  image_dest: "build/image",

  dist: "dist",
  clean: ["build/js/**",
          "build/html/**",
          "build/css/**",
          "build/font/**",
          "dist/*"],

  /* Paths to GZip */
  gzip: ["build/**/*.js",
         "build/**/*.html",
         "build/**/*.css"]
}

var devices = [
  { type : "mobile",
    name : "m",
    defs : {
      DEBUG:  false,
      MOBILE: true
    }
  },
  { type : "desktop",
    name : "d",
    defs : {
      DEBUG:  false,
      MOBILE: false
    }
  }
];

var domain = {
  dev: {
    //static: "127.0.0.1:9000/build"
    //static: "127.0.0.1:9090/editor"
    static: "/editor"
  },
  itg: {
    static: "127.0.0.1:9000/itg"
  },
  prod: {
    static: "127.0.0.1:9000/prod"
  }
};

/* version */
var pjson        = require("./package.json");
var dev_version  = pjson["dev-version"];
var lib_version  = pjson["lib-version"];
var version      = pjson.version;
var prod         = gutil.env.prod ? gutil.env.prod : false;
var integration  = gutil.env.integration ? gutil.env.integration : false;
var dev          = !prod && !integration;

/* Clean task */
gulp.task("clean", function(){
  del.sync(paths.clean);
});

/* Clean app js */
gulp.task("clean-js", function(){
  del.sync([paths.js_dest + "/*"]);
});

/* JSHint, concat */
var react   = require("gulp-react");
var jshint  = require("gulp-jshint");
var stylish = require("jshint-stylish");
gulp.task("js-compile", ["clean-js"], function(){

  var jsconf = prod ? "prod" : integration ? "itg" : "dev";
  var final_paths = paths.config[jsconf].concat(paths.js);
  var i18n_paths = dev ? paths.i18n_default : paths.i18n;

  for (var locale in paths.i18n) {
    var temp_paths = paths.i18n[locale].concat(final_paths);

    for(var index in devices) {
      var device = devices[index];

      var ret = gulp.src(temp_paths)
      .pipe(react())
      .pipe(jshint())
      .pipe(jshint.reporter(stylish))
      .pipe(concat("app-" + device.name + ".min.js"))
      .pipe(filesize())
      /*.pipe(uglify({
        compress: {
          global_defs: device.defs
        }
      }))*/
      .pipe(filesize())
      .pipe(gulpif(prod || integration, rename({basename: "app-" + device.name + "-" + locale + "-" + version + ".min"})))
      .pipe(gulp.dest(paths.js_dest))
      .on("error", gutil.log);
    }
  }
});

/* Concat lib */
gulp.task("lib-js-compile", ["js-compile"],function(){
  return gulp.src(paths.lib_js)
  .pipe(insert.append(";"))
  .pipe(concat("lib.min.js"))
  .pipe(filesize())
  .pipe(gulpif(prod || integration, rename({basename: "lib-" + lib_version + ".min"})))
  .pipe(gulp.dest(paths.js_dest))
  .on("error", gutil.log);
});

/* Clean app css */
gulp.task("clean-css", function(){
  del.sync([paths.css_dest + "/*"]);
});

/* CSS lint + minify + concat */
var csslint   = require("gulp-csslint");
var minifyCSS = require("gulp-minify-css");
var autoprefixer = require('gulp-autoprefixer');
gulp.task("css-compile", ["clean-css"], function() {
  return gulp.src(paths.css)
  .pipe(csslint({
    "box-sizing": false
  }))
  .pipe(csslint.reporter())
  .pipe(concat("app.min.css"))
  .pipe(autoprefixer({
    browsers: ["> 1%",
               "last 3 versions",
               "Firefox ESR",
               "Opera 12.1"],
    cascade: false
  }))
  .pipe(filesize())
  .pipe(minifyCSS())
  .pipe(filesize())
  .pipe(gulpif(prod || integration, rename({basename: "app-" + version + ".min"})))
	.pipe(gulp.dest(paths.css_dest))
  .on("error", gutil.log);
});

gulp.task("lib-css-compile", ["css-compile"], function() {
  return gulp.src(paths.lib_css)
  .pipe(concat("lib.min.css"))
  .pipe(filesize())
  .pipe(gulpif(prod || integration, rename({basename: "lib-" + lib_version + ".min"})))
  .pipe(gulp.dest(paths.css_dest))
  .on("error", gutil.log);
});

/* Clean app html */
gulp.task("clean-html", function(){
  del.sync([paths.html_dest + "/*"]);
});

/* HTML lint + replace + minify*/
var htmlreplace = require("gulp-html-replace");
var htmlhint    = require("gulp-htmlhint");
var htmlmin     = require("gulp-htmlmin");
gulp.task("html-compile", ["clean-html"], function(){

  var i18n_paths = dev ? paths.i18n_default : paths.i18n;

  for (var locale in i18n_paths) {

    for(var index in devices) {
      var device = devices[index];
     gulp.src(paths.html)
      .pipe(htmlreplace({
        'js-lib':  prod ? domain.prod.static + '/js/lib-' + lib_version + '.min.js' : integration ? domain.itg.static + '/js/lib-' + lib_version + '.min.js' :  domain.dev.static + '/js/lib.min.js',
        'js':      prod ? domain.prod.static + '/js/app-' + device.name + '-' + locale + '-' + version + '.min.js' : integration ?  domain.itg.static + '/js/app-' + device.name + '-' + locale + '-' + version + '.min.js' : domain.dev.static + '/js/app-' + device.name + '.min.js',
        'css-lib': prod ? domain.prod.static + '/css/lib-' + lib_version + '.min.css' : integration ?   domain.itg.static + '/css/lib-' + lib_version + '.min.css' : domain.dev.static + '/css/lib.min.css',
        'css':     prod ? domain.prod.static + '/css/app-' + version + '.min.css' : integration ?  domain.itg.static + '/css/app-' + version + '.min.css' :  domain.dev.static + '/css/app.min.css'
      }))
      .pipe(htmlhint())
      .pipe(htmlhint.reporter())
      .pipe(filesize())
      .pipe(htmlmin({collapseWhitespace: true, removeComments: true}))
      .pipe(filesize())
      .pipe(rename({suffix: '-' + locale + '-' + device.type }))
      .pipe(gulp.dest(paths.html_dest))
      .on('error', gutil.log);
    }
  }
});

/* Clean fonts */
gulp.task("clean-fonts", function(){
  del.sync([paths.font_dest + "/**"]);
});

/* Fonts copy lib */
gulp.task("fonts-copy-lib", ["clean-fonts"], function(){
  return gulp.src(paths.font)
  .pipe(gulp.dest(paths.font_dest));
});

/* Fonts init */
gulp.task("fonts-copy-init", function(){
  return gulp.src("bower_components/bootstrap/fonts/**/*")
  .pipe(gulp.dest("src/font"));
});

/* Clean images */
gulp.task("clean-image", function(){
  return del([paths.image_dest + "/**"]);
});

/* Images copy lib */
gulp.task("images-copy-lib", function(){
  return gulp.src(paths.image)
  .pipe(gulp.dest(paths.image_dest));
});

/* GZip static files */
var gzip = require("gulp-gzip");
gulp.task("gzip", ["default"], function() {
  return gulp.src(paths.gzip)
  .pipe(filesize())
  .pipe(gzip())
  .pipe(filesize())
  .pipe(gulp.dest("build"))
  .on("error", gutil.log);
});

/* Bump dev version*/
var bump = require("gulp-bump");
gulp.task("bump-dev", function(){
  return gulp.src("./package.json")
  .pipe(bump({key: "dev-version", type: "prerelease"}))
  .pipe(gulp.dest("./"));
});

/* Bump prod version*/
gulp.task("bump", function(){
  return gulp.src("./package.json")
  .pipe(bump({type: "prerelease"}))
  .pipe(gulp.dest("./"));
});

/* Bump lib version*/
gulp.task("bump-lib", function(){
  return gulp.src("./package.json")
  .pipe(bump({key: "lib-version", type: "minor"}))
  .pipe(gulp.dest("./"));
});

gulp.task("clean-dist", function(){
  return del.sync([paths.dist + "/*"]);
});

/* Package */
var zip = require("gulp-zip");
gulp.task("zip", ["clean-dist", "gzip"], function () {
  return gulp.src("build/**/*.*")
    .pipe(zip("idmu-editor.zip"))
    .pipe(filesize())
    .pipe(gulp.dest(paths.dist))
    .on("error", gutil.log);
});

/* Default task */
gulp.task("default", ["lib-js-compile", "lib-css-compile", "html-compile", "fonts-copy-lib", "images-copy-lib"]);


/* Package */
/* scp -i ~/.ssh/key.pem dist/idmu-editor.zip ysriram@10.0.1.204:/tmp */
/* unzip -o idmu-editor.zip -d /usr/local/idmu-editor */
gulp.task("package", ["zip", "bump"]);

gulp.task("server-node", function(){
  nodemon({
    script: "./tools/server/server.js",
    ignore: ["node_modules/**", "src/**", ".git/**"]
  });
});
