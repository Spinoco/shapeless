/*
 * Copyright (c) 2011 Alois Cochard 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import Keys._

object Publishing {
  import ShapelessBuild._

  val projectUrl    = "https://github.com/milessabin/shapeless"
  val developerId   = "milessabin"
  val developerName = "Miles Sabin"
  val licenseName   = "Apache License"
  val licenseUrl    = "http://www.apache.org/licenses/LICENSE-2.0.txt"

  val ossSnapshots  = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  val ossStaging    = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
  
  val licenseDistribution = "repo"
  val scmUrl              = projectUrl
  val scmConnection       = "scm:git:" + scmUrl

  def generatePomExtra(scalaVersion: String): xml.NodeSeq = {
    <url>{ projectUrl }</url>
      <licenses>
        <license>
          <name>{ licenseName }</name>
          <url>{ licenseUrl }</url>
          <distribution>{ licenseDistribution }</distribution>
        </license>
      </licenses>
    <scm>
      <url>{ scmUrl }</url>
      <connection>{ scmConnection }</connection>
    </scm>
    <developers>
      <developer>
        <id>{ developerId }</id>
        <name>{ developerName }</name>
      </developer>
    </developers>
  }

  def settings: Seq[Setting[_]] = Seq(
    credentialsSetting,
    publishMavenStyle := true,
    publishTo <<= (version).apply { v =>
      val nexus = "https://maven.spinoco.com/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("Snapshots" at nexus + "nexus/content/repositories/snapshots")
      else
        Some("Releases" at nexus + "nexus/content/repositories/releases")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false)
  )

  lazy val credentialsSetting =
    credentials += {
      Seq("build.publish.user", "build.publish.password").map(k => Option(System.getProperty(k))) match {
        case Seq(Some(user), Some(pass)) =>
          Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
        case _ =>
          Credentials(Path.userHome / ".ivy2" / ".credentials")
      }
    }
}
