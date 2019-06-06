# Cordova Android Pose Estimation Plugin

### Installation
`cordova plugin add https://github.com/lapin7771n/PoseEstimationAndroid.git`

### Info
**Required Android API level 21**

This plugin provides JS API for customization your skeleton and pose estimation processing.

**Example:**
```javascript
poseEstimation.initPoseEstimation({
      "cameraDevice": poseEstimation.CAMERA_FRONT,
      "convertingQuality": poseEstimation.ConvertingQuality.MEDIUM,
      "style": {
          "point": {
            "fillColor":   "#aabbcc",
            "strokeColor": "#aabbcc",
          },
          "line": {
             "strokeColor": "#aabbcc"
          }
        }
    }, function(success, error){
        //your code here...
    });
```
Sample of params:

```javascript
        const poseEstimation = {
                CAMERA_FRONT: "front",
                CAMERA_BACK: "back",
        
                CONVERSION_QUALITY_HIGH: "high",
                CONVERSION_QUALITY_MEDIUM: "medium",
                CONVERSION_QUALITY_LOW: "low"
                
                //....
        }
```


### Pose estimation on/off

Also, you can start/stop pose estimation processing with this code: 

#### Start:
`poseEstimation.startCapture(params, callback)`

**Example:**

```javascript
    poseEstimation.startCapture(params, function(success, error) {
              //your code here...
    })
```

Available params are:
```javascript
{
  "nmsRadius":     <number>,  // default: 30
  "outputStride":  <number>,  // One of 8, 16, 32, 64. Default: 16
  "scoreTreshold": <number>,  // starting from 1.0 to 0.0. Default - 0.5;
  "minConfidence": <number>   // starting from 1.0 to 0.0. Default - 0.05;
}
```

#### Stop:
To stop pose estimation processing you need to ues this:

`poseEstimation.stopCapture(callback)`

**Example:**

```javascript
    poseEstimation.stopCapture(function(success, error) {
        //your code here...      
    })
``` 


### Video recording:
To start video recording just call this method:

```javascript
    poseEstimation.startRecording(function(success, error) {
        //you code here...      
    })
```

To stop video recording call this method:

```javascript
    poseEstimation.stopRecording(function(success, error) {
        //your code here...      
    })
```
