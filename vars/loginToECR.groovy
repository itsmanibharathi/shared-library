def call(Map config) {
    def awsRegion = config.awsRegion ?: 'us-east-1'
    def ecrRepo = config.ecrRepo
    def credentialsId = config.credentialsId

    if (!ecrRepo) {
        error "Missing required parameters: ecrRepo"
    }
    
    sh """
    aws ecr get-login-password --region ${awsRegion} | docker login --username AWS --password-stdin ${ecrRepo}
    """
}