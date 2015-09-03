class DwcArchiveUrlMappings {
    static mappings = {
        "/ws/archive/check(.$rformat)?"(controller: 'archive', action: 'checkImageArchive') {
            format = { params.rformat ?: 'json' }
        }
        "/archive"(controller: 'archive', action: 'index')
        "/archive/check"(controller: 'archive', action: 'checkImageArchive') {
            format = 'html'
        }
        "500"(view: '/error')
    }
}
