package com.example.demo.service;

import com.example.demo.dto.KeystrokePoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CadenceVerificationService {

    // Threshold in milliseconds for invisible cadence RMSE
    private static final double RMSE_THRESHOLD = 95.0;

    public boolean verifyCadence(List<KeystrokePoint> enrolled, List<KeystrokePoint> attempt) {
        if (enrolled == null || attempt == null) return false;
        
        // Agar invisible trigger count mismatch hai (e.g. 3 Backspace vs 4 Backspace)
        if (enrolled.size() != attempt.size() || enrolled.isEmpty()) {
            System.out.println("[PhantomKey Stealth] Pattern length mismatch: expected " 
                    + enrolled.size() + ", got " + attempt.size());
            return false;
        }

        double sumSquaredError = 0.0;
        int dimensions = 0;

        for (int i = 0; i < enrolled.size(); i++) {
            KeystrokePoint e = enrolled.get(i);
            KeystrokePoint a = attempt.get(i);

            // Verify physical key identity (e.g., Backspace vs Shift)
            if (!e.getKey().equalsIgnoreCase(a.getKey())) {
                System.out.println("[PhantomKey Stealth] Structural key mismatch at index " + i);
                return false;
            }

            // Dwell time deviation (hold duration of the stealth key)
            double dwellDiff = e.getDwellTime() - a.getDwellTime();
            sumSquaredError += Math.pow(dwellDiff, 2);
            dimensions++;

            // Flight time deviation (gap between stealth triggers)
            if (i > 0) {
                double flightDiff = e.getFlightTime() - a.getFlightTime();
                sumSquaredError += Math.pow(flightDiff, 2);
                dimensions++;
            }
        }

        double rmse = Math.sqrt(sumSquaredError / dimensions);
        System.out.println(String.format(
            "[PhantomKey Stealth Engine] Computed Cadence RMSE: %.2f ms (Threshold: %.1f ms)",
            rmse, RMSE_THRESHOLD
        ));

        return rmse <= RMSE_THRESHOLD;
    }
}