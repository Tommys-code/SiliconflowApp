plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi-ooxml:5.2.3")  // 支持.xlsx格式
    implementation("com.google.code.gson:gson:2.10.1") // JSON序列化
}
