var fs = require('fs-extra')
var path = require('path')
var glob = require('glob')
var sass = require('node-sass')

var config = {
 src: 'app/assets/styles/scss/**/!(_)*.scss',
 dest: 'app/views/tags'
}

var writeFile = function (filepath, content) {
  fs.access(filepath, function (err) {
    if(err) {
      fs.mkdirpSync(path.dirname(err.path))
    }

    fs.writeFile(filepath, content, function (err) {
      if(err) {
        return console.log(err)
      }

      var fileName = path.basename(filepath)
      console.log('[SUCCESS] ' + fileName)
    })
  })
}

glob(config.src, function (err, files) {
  console.log('Compiling styles...')

  if(err) {
    return console.log(err)
  }

  files.forEach(function (file) {
    sass.render({
      file,
      includePaths: config.includePaths
    }, function (err, result) {
      if (err) {
        return console.log(err);
      }

      const fileName = path.basename(file, '.scss').split('-')[0]
      writeFile(config.dest + '/styles_' + fileName + '.scala.html', result.css)
    })
  })
})
