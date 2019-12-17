#!/usr/bin/env groovy

class SlackNotifyConfig {
  boolean notifyStart = false
  boolean notifySuccess = false
  boolean notifyAborted = true
  boolean notifyNotBuilt = true
  boolean notifyUnstable = true
  boolean notifyFailure = true
  boolean notifyRepeatedFailure = false
  boolean notifyBackToNormal = true
}

def call(def currentBuild, Map configMap = [:]) {
  def config = configMap as SlackNotifyConfig
  echo "slackNotify library called with config: ${config.dump()}"
}
