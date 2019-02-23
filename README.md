# 一、简介

#### 此项目是Android开发中的快速绑定View的工具，使用注解绑定指定的View或方法，通过编译时生成相应代码和方法，以此可大大减少平常开发当中的工作量。


# 二、使用

#### 1.引入库
###### 首先在Project的Gradle中引入
```
classpath 'org.liang.plugin:injector_tools:1.0.5'
```
###### 然后在要使用此库的module的Gradle中引入插件
```
apply plugin: 'com.liang.inject'
```

#### 2. 绑定View：@BindView(id)

##### 1.在application中绑定

###### Java代码
```
@BindView(R.id.button)
Button button;

@BindView(R.id.imageView)
ImageView imageView;

@BindView(R.id.textView)
TextView textView;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    JInjector.bind(this);
}
```
###### Kotlin代码
```
@BindView(R.id.button)
lateinit var button: Button

@BindView(R.id.imageView)
lateinit var imageView: ImageView

@BindView(R.id.textView)
lateinit var textView: TextView

//别忘了bind额
JInjector.bind(this);
```
##### 2.在library中绑定

###### 注：在library中由于R文件中的Id不是常量，所以通过gradle编译时生产了对应的R2文件，使用时用R2代替R即可

###### Java代码
```
@BindView(R2.id.button)
Button button;
```
###### Kotlin代码
```
@BindView(R2.id.button)
lateinit var button: Button
```
#### 3.绑定View的监听方法
##### 目前只支持以下常用的方法：
###### @OnClick、@OnLongClick、@OnCheckedChanged、@OnTextChanged、@OnEditorAction
##### 举个栗子:
###### 以前这样写
```
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    }
});

imageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    }
});

textView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    }
});

...
```
###### 现在这样写
```
@OnClick({R.id.button, R.id.imageView,R.id.textView,...})
public void test(View view) {
   Log.e("TestActivity", "view: " + view.getId());
   ...
}
```
##### 注：方法参数除了第一个为View时可以省略以外，其余的参数必须对应其监听方法的回调参数
