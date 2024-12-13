def call(Map config) {
    def dockerImage = config.dockerImage
    def dockerTag = config.dockerTag ?: 'latest'
    def ecrRepo = config.ecrRepo

    if (!dockerImage || !ecrRepo) {
        error "Missing required parameters: dockerImage, ecrRepo"
    }

    sh "docker push ${ecrRepo}/${dockerImage}:${dockerTag}"
}