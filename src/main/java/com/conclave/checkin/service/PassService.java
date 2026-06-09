package com.conclave.checkin.service;

import com.conclave.checkin.model.Attendee;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

@Service
public class PassService {

    public File generatePass(
            Attendee attendee,
            File qrFile
    ) throws Exception {

        BufferedImage template =
                ImageIO.read(
                        getClass().getResourceAsStream(
                                "/templates/pass-template.png"
                        )
                );

        Graphics2D g = template.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Better rendering quality
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        BufferedImage photo =
                ImageIO.read(
                        new URL(attendee.getPhotoUrl())
                );

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        BufferedImage qr =
                ImageIO.read(qrFile);

        // ======================
        // PHOTO
        // ======================

        // PHOTO
        g.drawImage(
                photo,
                95,
                65,     // was 95
                145,
                165,
                null
        );

        // ======================
        // NAME
        // ======================

        // NAME
        g.setColor(new Color(0, 32, 96));

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        drawCenteredText(
                g,
                attendee.getFullName().toUpperCase(),
                168,
                265
        );

        // ======================
        // DESIGNATION
        // ======================

        // DESIGNATION
        g.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        drawCenteredText(
                g,
                attendee.getDesignation(),
                168,
                280
        );
        // ======================
        // ORGANIZATION
        // ======================

        // ORGANIZATION
        drawCenteredText(
                g,
                attendee.getOrganizationName(),
                168,
                300
        );

        // ======================
        // QR CODE
        // ======================

        // QR CODE
        g.drawImage(
                qr,
                108,    // x
                310,    // y
                120,    // width
                120,    // height
                null
        );
        g.dispose();

        File pass =
                new File(
                        "pass-" +
                                attendee.getUid() +
                                ".png"
                );

        ImageIO.write(
                template,
                "png",
                pass
        );

        return pass;
    }

    private void drawCenteredText(
            Graphics2D g,
            String text,
            int centerX,
            int y
    ) {

        FontMetrics fm =
                g.getFontMetrics();

        int x =
                centerX -
                        (fm.stringWidth(text) / 2);

        g.drawString(
                text,
                x,
                y
        );
    }
}