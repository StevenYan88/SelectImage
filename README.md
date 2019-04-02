#### Android--选择多张图片，支持拖拽删除、排序、预览图片

**效果图**
![2019-03-28_11_45_57.gif](https://upload-images.jianshu.io/upload_images/1472453-e0153839f1a9a8b9.gif?imageMogr2/auto-orient/strip)  

截图1 | 截图2 | 截图 3 | 截图 4 
---|---|---|---
| ![异步加载图片](https://upload-images.jianshu.io/upload_images/1472453-9e417e5f60ebdaa5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)| ![图片文件夹](https://upload-images.jianshu.io/upload_images/1472453-7592bab5f2c2c0a8.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)|![选择图片](https://upload-images.jianshu.io/upload_images/1472453-6fea963cf69a4560.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)|![已选择的图片](https://upload-images.jianshu.io/upload_images/1472453-f7708e64f82e4c62.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

   
 ##### 具体思路（知识点）：  
    1. 异步加载相册图片；  
    2. 自定义相册文件夹；  
    3. 支持单选、多选（最多9张）图片；  
    4. ItemTouchHelper实现拖拽、排序、删除；  
    5. 对Canvas画布操作、变换，结合属性动画，实现图片的放大、缩小等；  
    
**详细看代码怎么实现的！！！**

**License**  

    Copyright 2019 StevenYan88
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0  
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
