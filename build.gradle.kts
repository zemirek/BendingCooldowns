plugins {
	id("java")
	id("io.papermc.paperweight.userdev") version "1.7.4"
}

group = "me.literka"
version = "1.0"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
	maven("https://jitpack.io")
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	paperweight.paperDevBundle("1.18-R0.1-SNAPSHOT")
	compileOnly("com.github.ProjectKorra:ProjectKorra:v1.11.2")
}