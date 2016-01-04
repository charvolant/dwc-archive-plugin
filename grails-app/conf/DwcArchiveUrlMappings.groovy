class DwcArchiveUrlMappings {
    static mappings = {
        "/ws/archive/check(.$rformat)?"(controller: 'archive', action: 'checkImageArchive') {
            format = { params.rformat ?: 'json' }
        }
        "/ws/archive/flatten"(controller: 'archive', action: 'flattenMeasurementArchiveComplete')
        "/archive"(controller: 'archive', action: 'index')
        "/archive/$action"(controller: 'archive')
        "500"(view: '/error')
    }
}
