def call(Map config) {
    def dockerImage = config.dockerImage
    def dockerTag = config.dockerTag ?: 'latest'

    if (!dockerImage) {
        error "Missing required parameter: dockerImage"
    }

    sh "docker build -t ${dockerImage}:${dockerTag} ."
}