Пока на этапе разработки чтобы добавить в проект 

1 Собрать этот проект
2 В зависимом проекте добавить в POM

    <repositories>
        <repository>
            <id>CommonLibRepo</id>
            <name>Common Lib repo</name>
            <url>file://${user.home}/MyOwnProjects/COOL-ETP-GH/cool-etp/backend/common/target</url>
        </repository>
    </repositories>
    
        <dependency>
            <groupId>org.cooletp</groupId>
            <artifactId>cooletp-common</artifactId>
            <version>1.0</version>
        </dependency>
        
При необходимости путь скорректировать