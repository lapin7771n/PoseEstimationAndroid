//package com.nlapin.poseestimation;
//
//import androidx.camera.core.CameraX;
//
//public class PluginConfig {
//
//    /**
//     * Camera device
//     * Default - BACK
//     */
//    public static CameraLens CAMERA_LENS = CameraLens.BACK;
//
//    /**
//     * Quality of converting image from camera
//     * Default - MEDIUM
//     */
//    public static ConvertingQuality CONVERTING_QUALITY = ConvertingQuality.MEDIUM;
//
//    /**
//     * Is skeleton showing
//     * Default - true
//     */
//    public static boolean SHOW_SKELETON = true;
//
//    /**
//     * Is box showing around human
//     * Default - true
//     */
//    public static boolean SHOW_BOX = true;
//
//    /**
//     * Styling class for skeleton drawing
//     */
//    public class Style {
//
//        public class Point {
//
//            /**
//             * Color that fills point
//             * Default - BLACK
//             */
//            public String FILL_COLOR = "#000";
//
//            /**
//             * Border color of the point
//             * Default - BLACK
//             */
//            public String STROKE_COLOR = "#000";
//        }
//
//        public class Line {
//
//            /**
//             * Color of the line
//             * Default - RED
//             */
//            public String STROKE_COLOR = "#ff0000";
//        }
//    }
//
//    public enum CameraLens {
//        FRONT(CameraX.LensFacing.FRONT),
//        BACK(CameraX.LensFacing.BACK);
//
//        private final CameraX.LensFacing lensFacing;
//
//        CameraLens(CameraX.LensFacing lensFacing) {
//            this.lensFacing = lensFacing;
//        }
//
//        CameraX.LensFacing getValue() {
//            return lensFacing;
//        }
//    }
//
//    public enum ConvertingQuality {
//        LOW(10),
//        MEDIUM(50),
//        HIGH(100);
//
//        private final int value;
//
//        ConvertingQuality(int i) {
//            this.value = i;
//        }
//
//        public int getValue() {
//            return value;
//        }
//    }
//}
