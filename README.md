# Eatery Blue - Browse. Search. Eat.

<p align="center"><img src="https://github.com/cuappdev/assets/blob/master/app-icons/Eatery-83.5x83.5%402x.png" width=210 /></p>

Eatery was the first app released by AppDev.  With over 6,000 students using it every month, it enables students to browse menus and discover places to eat on Cornell’s campus. It enhances the dining experience at Cornell with features such as providing the crowdedness of eateries, checking meal swipes and dining money balances, and favoriting dishes. Eatery is available on both [iOS](https://github.com/cuappdev/eatery-blue-ios) and Android platforms.

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:cuappdev/eatery-blue-android.git
```

## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

## Maintainers
This project is maintained by the [Android Team @ Cornell Appdev](https://www.cornellappdev.com/team)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Run the linter: https://developer.android.com/studio/write/lint
5. Push your branch (git push origin my-new-feature)
6. Create a new Pull Request

## Internal Testing
1. In the `main` branch, push a PR updating the `whatsnew-en-US` file as well as `versionCode` and `versionName` in the app `build.gradle`.
2. Create a PR from `main` to `prod-beta`. Once merged, this will trigger the GitHub Action that publishes to the play store, as defined in `publish-to-play-store-beta.yml`.
3. Open Eatery in the Google Play Console. In "Latest releases and bundles", you should see the new app bundle. Navigate to the new bundle to access the internal app sharing link. Once you set up proper access (update the email list), those in the list should be able to install this bundle.