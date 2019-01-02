/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import CALM_.ROIFitter.AnalysePlate;
import IO.File.ImageFilter;
import IO.PropertyWriter;
import Math.Optimisation.Plate;
import UIClasses.GUIMethods;
import UIClasses.PropertyExtractor;
import UIClasses.UIMethods;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.IJ;
import ij.ImagePlus;
import java.awt.Container;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class PlateFitterUI extends javax.swing.JFrame implements GUIMethods {

    public static final String N_ROWS = "Number of Rows:";
    public static final String N_COLS = "Number of Columns:";
    public static final String SPAT_RES = "Spatial Resolution (mm/pixel):";
    public static final String WELL_RAD = "Well Radius (mm):";
    public static final String X_BUFF = "X Margin (mm):";
    public static final String Y_BUFF = "Y Margin (mm):";
    public static final String WELL_FRACTION = "Well Fraction:";
    public static final String WELL_SPACING = "Inter-Well Spacing (mm):";
    public static final String X_POS = "X Position (mm):";
    public static final String Y_POS = "Y Position (mm):";
    public static final String ANGLE = "Angle (Degrees):";
    public static final String SHRINK_FACTOR = "Shrink Factor:";

    private static int rows = 2;
    private static int cols = 3;
    private static int wellRad = 86;
    private static double xBuff = 30.0;
    private static double yBuff = 20.0;
    private static double wellFraction = 0.9;
    private static double interWellSpacing = 10;
    private static double xLoc;
    private static double yLoc;
    private static double angle;
    private static double spatRes = 1.0;
    private static double shrinkFactor = 1.0;
    private final String TITLE = "Plate Analyser";
    private Properties props;
    private static File inputDirectory;
    private ImagePlus imp;

    /**
     * Creates new form PlateFitterUI
     */
    public PlateFitterUI() {
        initComponents();
        this.setTitle(TITLE);
        UIMethods.centreContainer(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        directorySelectTextField = new javax.swing.JTextField();
        directorySelectButton = new javax.swing.JButton();
        rowTextField = new javax.swing.JTextField();
        colTextField = new javax.swing.JTextField();
        wellRadTextField = new javax.swing.JTextField();
        interWellTextField = new javax.swing.JTextField();
        xBuffTextField = new javax.swing.JTextField();
        yBuffTextField = new javax.swing.JTextField();
        rowLabel = new javax.swing.JLabel();
        colLabel = new javax.swing.JLabel();
        wellRadLabel = new javax.swing.JLabel();
        interWellLabel = new javax.swing.JLabel();
        xBuffLabel = new javax.swing.JLabel();
        yBuffLabel = new javax.swing.JLabel();
        spatResTextField = new javax.swing.JTextField();
        spatResLabel = new javax.swing.JLabel();
        previewButton = new javax.swing.JButton();
        xPosLabel = new javax.swing.JLabel();
        xPosTextField = new javax.swing.JTextField();
        yPosLabel = new javax.swing.JLabel();
        yPosTextField = new javax.swing.JTextField();
        angleLabel = new javax.swing.JLabel();
        angleTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        wellFractionLabel = new javax.swing.JLabel();
        wellFractionTextField = new javax.swing.JTextField();
        shrinkFactorLabel = new javax.swing.JLabel();
        shrinkFactorTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(550, 550));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        directorySelectTextField.setText(inputDirectory!=null ? inputDirectory.getAbsolutePath() : "Select input directory...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(directorySelectTextField, gridBagConstraints);

        directorySelectButton.setText("Select");
        directorySelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directorySelectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(directorySelectButton, gridBagConstraints);

        rowTextField.setText(String.valueOf(rows));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(rowTextField, gridBagConstraints);

        colTextField.setText(String.valueOf(cols));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(colTextField, gridBagConstraints);

        wellRadTextField.setText(String.valueOf(wellRad));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(wellRadTextField, gridBagConstraints);

        interWellTextField.setText(String.valueOf(interWellSpacing));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(interWellTextField, gridBagConstraints);

        xBuffTextField.setText(String.valueOf(xBuff));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(xBuffTextField, gridBagConstraints);

        yBuffTextField.setText(String.valueOf(yBuff));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(yBuffTextField, gridBagConstraints);

        rowLabel.setText(N_ROWS);
        rowLabel.setLabelFor(rowTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(rowLabel, gridBagConstraints);

        colLabel.setText(N_COLS);
        colLabel.setLabelFor(colTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(colLabel, gridBagConstraints);

        wellRadLabel.setText(WELL_RAD);
        wellRadLabel.setLabelFor(wellRadTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(wellRadLabel, gridBagConstraints);

        interWellLabel.setText(WELL_SPACING);
        interWellLabel.setLabelFor(interWellTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(interWellLabel, gridBagConstraints);

        xBuffLabel.setText(X_BUFF);
        xBuffLabel.setLabelFor(xBuffTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(xBuffLabel, gridBagConstraints);

        yBuffLabel.setText(Y_BUFF);
        yBuffLabel.setLabelFor(yBuffTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(yBuffLabel, gridBagConstraints);

        spatResTextField.setText(String.valueOf(spatRes));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(spatResTextField, gridBagConstraints);

        spatResLabel.setText(SPAT_RES);
        spatResLabel.setLabelFor(spatResTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(spatResLabel, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getContentPane().add(previewButton, gridBagConstraints);

        xPosLabel.setText(X_POS);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(xPosLabel, gridBagConstraints);

        xPosTextField.setText(String.valueOf(xLoc));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(xPosTextField, gridBagConstraints);

        yPosLabel.setText(Y_POS);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(yPosLabel, gridBagConstraints);

        yPosTextField.setText(String.valueOf(yLoc));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(yPosTextField, gridBagConstraints);

        angleLabel.setText(ANGLE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(angleLabel, gridBagConstraints);

        angleTextField.setText(String.valueOf(angle));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(angleTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        getContentPane().add(jSeparator2, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(okButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(cancelButton, gridBagConstraints);

        wellFractionLabel.setText(WELL_FRACTION);
        wellFractionLabel.setLabelFor(wellFractionTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(wellFractionLabel, gridBagConstraints);

        wellFractionTextField.setText(String.valueOf(wellFraction));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(wellFractionTextField, gridBagConstraints);

        shrinkFactorLabel.setText(SHRINK_FACTOR);
        shrinkFactorLabel.setLabelFor(shrinkFactorTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(shrinkFactorLabel, gridBagConstraints);

        shrinkFactorTextField.setText(String.valueOf(shrinkFactor));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(shrinkFactorTextField, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void directorySelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directorySelectButtonActionPerformed
        try {
            inputDirectory = Utilities.getFolder(inputDirectory, "Select input directory...", true);
        } catch (Exception e) {
            cleanUp();
        }
        if (inputDirectory == null) {
            cleanUp();
            return;
        }
        directorySelectTextField.setText(inputDirectory.getAbsolutePath());
    }//GEN-LAST:event_directorySelectButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cleanUp();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (!setVariables()) {
            return;
        }
        File outputDirectory = new File(GenUtils.openResultsDirectory(String.format("%s%s%s%s", inputDirectory.getAbsolutePath(), File.separator, TITLE, "_Results")));
        try {
            PropertyWriter.printProperties(props, outputDirectory.getAbsolutePath(), TITLE, true);
        } catch (Exception e) {
            IJ.log("Failed to save property file.");
        }
        cleanUp();
        (new AnalysePlate(props, inputDirectory, outputDirectory)).analyse();
    }//GEN-LAST:event_okButtonActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        if (!setVariables()) {
            return;
        }
        String[] fileList = inputDirectory.list(new ImageFilter(new String[]{"tif", "tiff", "png"}));
        String filename = String.format("%s%s%s", inputDirectory.getAbsolutePath(), File.separator, fileList[0]);
        if (imp == null) {
            imp = IJ.openImage(filename);
        }
        Plate plate = new Plate(rows, cols, wellRad / spatRes, xBuff / spatRes, yBuff / spatRes, interWellSpacing / spatRes, wellFraction);
        imp.setOverlay(plate.drawOverlay(xLoc / spatRes, yLoc / spatRes, angle));
        imp.show();
    }//GEN-LAST:event_previewButtonActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(PlateFitterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(PlateFitterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(PlateFitterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(PlateFitterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new PlateFitterUI().setVisible(true);
//            }
//        });
//    }
    public void setProperties(Properties p, Container container) {
        PropertyExtractor.setProperties(p, container, PropertyExtractor.WRITE);
    }

    public boolean setVariables() {
        inputDirectory = new File(directorySelectTextField.getText());
        if (!inputDirectory.exists()) {
            GenUtils.error(String.format("%s is not a valid input directory", inputDirectory.getPath()));
            return false;
        }
        rows = Integer.parseInt(rowTextField.getText());
        cols = Integer.parseInt(colTextField.getText());
        wellRad = Integer.parseInt(wellRadTextField.getText());
        xBuff = Double.parseDouble(xBuffTextField.getText());
        yBuff = Double.parseDouble(yBuffTextField.getText());
        interWellSpacing = Double.parseDouble(interWellTextField.getText());
        wellFraction = Double.parseDouble(wellFractionTextField.getText());
        xLoc = Double.parseDouble(xPosTextField.getText());
        yLoc = Double.parseDouble(yPosTextField.getText());
        angle = Double.parseDouble(angleTextField.getText());
        spatRes = Double.parseDouble(spatResTextField.getText());
        shrinkFactor = Double.parseDouble(shrinkFactorTextField.getText());
        setProperties(props, this);
        return true;
    }

    void cleanUp() {
        if (imp != null) {
            imp.close();
        }
        this.dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angleLabel;
    private javax.swing.JTextField angleTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel colLabel;
    private javax.swing.JTextField colTextField;
    private javax.swing.JButton directorySelectButton;
    private javax.swing.JTextField directorySelectTextField;
    private javax.swing.JLabel interWellLabel;
    private javax.swing.JTextField interWellTextField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton okButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel rowLabel;
    private javax.swing.JTextField rowTextField;
    private javax.swing.JLabel shrinkFactorLabel;
    private javax.swing.JTextField shrinkFactorTextField;
    private javax.swing.JLabel spatResLabel;
    private javax.swing.JTextField spatResTextField;
    private javax.swing.JLabel wellFractionLabel;
    private javax.swing.JTextField wellFractionTextField;
    private javax.swing.JLabel wellRadLabel;
    private javax.swing.JTextField wellRadTextField;
    private javax.swing.JLabel xBuffLabel;
    private javax.swing.JTextField xBuffTextField;
    private javax.swing.JLabel xPosLabel;
    private javax.swing.JTextField xPosTextField;
    private javax.swing.JLabel yBuffLabel;
    private javax.swing.JTextField yBuffTextField;
    private javax.swing.JLabel yPosLabel;
    private javax.swing.JTextField yPosTextField;
    // End of variables declaration//GEN-END:variables
}
