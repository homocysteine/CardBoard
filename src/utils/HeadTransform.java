package utils;

public class HeadTransform {
    private static final double GIMBAL_LOCK_EPSILON = 0.01f;
    private final double[] mHeadView;

    public HeadTransform() {
        super();
        Matrix.setIdentityM(this.mHeadView = new double[16], 0);
    }

    public double[] getHeadView() {
        return this.mHeadView;
    }

    public void getQuaternion(final double[] quaternion, final int offset) {
        if (offset + 4 > quaternion.length) {
            throw new IllegalArgumentException("Not enough space to write the result");
        }
        final double[] m = this.mHeadView;
        final double t = m[0] + m[5] + m[10];
        double w;
        double x;
        double y;
        double z;
        if (t >= 0.0f) {
            double s = Math.sqrt(t + 1.0f);
            w = 0.5f * s;
            s = 0.5f / s;
            x = (m[9] - m[6]) * s;
            y = (m[2] - m[8]) * s;
            z = (m[4] - m[1]) * s;
        }
        else if (m[0] > m[5] && m[0] > m[10]) {
            double s = Math.sqrt(1.0f + m[0] - m[5] - m[10]);
            x = s * 0.5f;
            s = 0.5f / s;
            y = (m[4] + m[1]) * s;
            z = (m[2] + m[8]) * s;
            w = (m[9] - m[6]) * s;
        }
        else if (m[5] > m[10]) {
            double s = Math.sqrt(1.0f + m[5] - m[0] - m[10]);
            y = s * 0.5f;
            s = 0.5f / s;
            x = (m[4] + m[1]) * s;
            z = (m[9] + m[6]) * s;
            w = (m[2] - m[8]) * s;
        }
        else {
            double s = Math.sqrt(1.0f + m[10] - m[0] - m[5]);
            z = s * 0.5f;
            s = 0.5f / s;
            x = (m[2] + m[8]) * s;
            y = (m[9] + m[6]) * s;
            w = (m[4] - m[1]) * s;
        }
        quaternion[offset + 0] = x;
        quaternion[offset + 1] = y;
        quaternion[offset + 2] = z;
        quaternion[offset + 3] = w;
    }
}
