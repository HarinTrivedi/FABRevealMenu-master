# FABRevealMenu 2.0 ✌️
***
An simple general purpose UI library to create custom user defied menu in modern way with your favourite Floating action button. It incorporates lollipop circular reveal view with cool animations somewhat inspired from google material design guideline <https://material.google.com/components/buttons-floating-action-button.html#buttons-floating-action-button-transitions>

Uses [Material Container Transformation](https://material.io/develop/android/theming/motion#container-transform) from material components by Google.
***

gradle dependency:

Step 1. Add it in your root build.gradle at the end of repositories:

````
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
````
Step 2. Add the dependency

````
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
....

dependencies {
        implementation 'com.github.HarinTrivedi:FABRevealMenu-master:2.0.1'
}
    
````


## What's new:
* Vector icon support
* Font support
* Disable menut item
* Set menu corder radius
* Set custom animation duration

## Demo
* Horizontal Menu

![demo_horizontal](https://i.imgur.com/238nJX7.gif)

* Vertical Menu

![demo_vertical](https://i.imgur.com/OQwH1ls.gif)

* Custom view in Menu

![demo_custom](https://i.imgur.com/FMrFoHs.gif)

***
## How to use
* By xml

Add namespace in layout like: 

    xmlns:app="http://schemas.android.com/apk/res-auto"

Use FABRevealMenu in xml layout like:

    <com.hlab.fabrevealmenu.view.FABRevealMenu
        android:id="@+id/fabMenu"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true"
        app:menuBackgroundColor="@colorRes"
        app:menuDirection="left/top/right/bottom"
        app:menuRes="@menuRes"
        app:menuTitleTextColor="@colorRes"
        app:showOverlay="true/false"
        app:showTitle="true/false"
        app:overlayBackground="@colorRes"
        app:menuSize="normal/small"
        app:duration="1000"
        app:menuCornerRadius="15dp"
        app:menuTitleFontFamily="@font/quicksand"
        app:menuTitleDisabledTextColor="@colorRes""/>
    


* By Code

All attributes can also be set/altered by below methods:

````
    setMenu(@MenuRes) // set R.menu resourece
    setMenuItems(ArrayList<FABMenuItem>) // set custom menu items
    setOverlayBackground(@ColorRes) // change default overlay background color 
    setMenuBackground(@ColorRes) // change menu background color 
    setShowOverlay(boolean) // change overlay visibility : be careful to use this
    setTitleVisible(boolean) // set menu item title visibility
    setMenuTitleTextColor(@ColorRes) // change menu item text color
    setMenuDirection(Direction) // change menu revealDirection when showed : place FAB on screen properly to change directions
    setSmallerMenu() // small size menu
    setNormalMenu() // normal size menu
    setMenuTitleDisabledTextColor(@ColorRes) // set disable text color
    setMenuTitleTypeface(@FontRes) // set custom font typeface

````

* Set custom view

You can inflate custom view by two ways

1. **app:menuCustomView="@layoutRes"** // you can get custom view object by calling getCustomView() on fabRevealMenu instance

2. **setCustomView(View)**

***

## LICENSE
````
Copyright 2022 Harry's Lab

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````
