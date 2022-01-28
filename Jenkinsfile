#!groovy

def tag = BRANCH_NAME.replaceAll(/[^a-zA-Z0-9_-]/, "-").toLowerCase()

// Record original branch or pull request for cleanup jobs
def branch = env.CHANGE_ID == null ? BRANCH_NAME : null
def pr = env.CHANGE_ID

def dockerRepository = 'registry.developmentgateway.org'

stage('Build') {
    node {
        withEnv(['DOCKER_BUILDKIT=1']) {
            try {
                checkout scm

                def image = docker.build("${dockerRepository}/iati-importer-ui:${tag}",
                        "--build-arg BUILD_SOURCE=${tag} " +
                        "--build-arg PULL_REQUEST=${pr} " +
                        "--build-arg BRANCH=${branch} " +
                        "./import-core")
                image.push()
            } finally {
                sh "docker rmi ${dockerRepository}/iati-importer-ui:${tag}"
            }
        }
    }
}

stage('Deploy') {

    def deployParams

    timeout(time: 1, unit: 'HOURS') {
        deployParams = input message: 'Ready to go?', parameters: [
                string(name: 'AMP_HOST', defaultValue: 'amp-haiti-develop.stg.ampsite.net',
                        description: 'The url of the AMP to link to. Must start with \'amp\' ' +
                                'and end with \'.stg.ampsite.net\'.', trim: true),
                string(name: 'COUNTRY', defaultValue: 'HT', description: 'Country ISO2 Code.', trim: true),
                string(name: 'IATI_ID_FIELD', defaultValue: 'project_code', description: 'IATI ID Field.', trim: true),
                string(name: 'PROCESSOR_VERSION', defaultValue: '3x', description: 'IATI Processor Version.',
                        trim: true)]
    }

    node {
        def host = deployParams['AMP_HOST']
        def country = deployParams['COUNTRY']
        def iatiField = deployParams['IATI_ID_FIELD']
        def processorVersion = deployParams['PROCESSOR_VERSION']
        sh "ssh boad.aws.devgateway.org 'iati-up $tag $host $country $iatiField $processorVersion'"
    }
}
