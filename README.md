纯命令编译与构建 Java 项目

> 部分脚本依赖 PowerShell 7

### 单个文件

##### 编译

```
javac -d .\build\classes\ .\com\icuxika\Main.java
```

##### Java 运行

```
java --class-path .\build\classes\ com.icuxika.Main
```

##### 创建 jar 包

```
jar -cef com.icuxika.Main .\build\libs\hello.jar .\build\classes\
```

### 多个文件

##### 编译

```
Get-ChildItem -Path .\src\ -Recurse -Filter *.java | Select-Object -ExpandProperty FullName | Out-File sources.txt; javac -d .\build\classes\ "@sources.txt"; Remove-Item sources.txt
```

##### Java 运行

```
java --class-path .\build\classes\ com.icuxika.Main
```

##### 创建 jar 包

```
jar -cef com.icuxika.Main .\build\libs\hello.jar -C .\build\classes\ .
```

> jar --create --file .\build\libs\hello.jar --main-class com.icuxika.Main -C .\build\classes\ .

##### 运行 jar 包

```
java -jar .\build\libs\hello.jar
```

### 引入三方库

##### 下载第三方 jar 包

```
mkdir library
Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar -OutFile .\library\gson-2.10.1.jar
```

##### 编译

```
Get-ChildItem -Path .\src\ -Recurse -Filter *.java | Select-Object -ExpandProperty FullName | Out-File sources.txt; javac --class-path .\library\* -d .\build\classes\ "@sources.txt"; Remove-Item sources.txt
```

##### Java 运行

```
java --class-path ".\library\*;.\build\classes\" com.icuxika.Main
```

##### 创建 jar 包

```
"Class-Path: " + (Get-ChildItem -Path .\library\ -Filter *.jar | Select-Object -ExpandProperty Name | Join-String -Separator " ") | Out-File Manifest.txt
```

```
jar --create --file .\build\libs\hello.jar -m Manifest.txt --main-class com.icuxika.Main -C .\build\classes\ .
```

> jar -cemf com.icuxika.Main Manifest.txt .\build\libs\hello.jar -C .\build\classes\ .

```
Get-ChildItem -Path .\library\ -Filter *.jar | Select-Object -ExpandProperty FullName | ForEach-Object { Copy-Item $_ -Destination .\build\libs\ }
```

```
Remove-Item .\Manifest.txt
```

##### 运行 jar 包

```
java -jar .\build\libs\hello.jar
```

##### 使用 jpackage 为上面生成的非模块化 jar 包生成应用程序映像

> `--win-console`用来支持标准输出流`System.out.println`

```
jpackage.exe --type app-image -i .\build\libs\ -n Hello --main-class com.icuxika.Main --main-jar .\hello.jar --dest .\build\image --win-console
```

##### 运行生成的 exe

```
.\build\image\Hello\Hello.exe
```

### 待做

-   项目模块化
-   jlink 与 jpackage

##### 在目录 src 下新建文件`module-info.java`

```
module hello {
    requires com.google.gson;

    opens com.icuxika.entity to com.google.gson;

    exports com.icuxika;
}
```

##### 编译

```
Get-ChildItem -Path .\src\ -Recurse -Filter *.java | Select-Object -ExpandProperty FullName | Out-File sources.txt; javac --module-path .\library\ -d .\build\classes\ "@sources.txt"; Remove-Item sources.txt
```

##### Java 运行

```
java --module-path ".\library\;.\build\classes\" -m "hello/com.icuxika.Main"
```

##### 创建 jar 包

> 与`引入三方库`一致

##### 运行 jar 包

> 与`引入三方库`一致

##### 使用 jlink 创建运行时映像，生成`.\build\jlink-build-dir\bin\Hello.bat`

```
jlink.exe --module-path .\build\libs\ --add-modules java.base,hello --launcher Hello=hello/com.icuxika.Main --compress=2 --no-header-files --no-man-pages --strip-debug --output .\build\jlink-build-dir
```
