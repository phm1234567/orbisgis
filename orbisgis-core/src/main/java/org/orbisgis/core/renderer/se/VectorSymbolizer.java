/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * This class contains common element shared by Point,Line,Area an Text Symbolizer.
 * Those vector layers all contains an unit of measure (Uom) and an affine transformation def (transform)
 * @author maxence
 */
public abstract class VectorSymbolizer extends Symbolizer {

    protected VectorSymbolizer(){
    }


    protected VectorSymbolizer(JAXBElement<? extends SymbolizerType> st) {
        super(st);
    }

    public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException;

    /**
     * Convert a spatial feature into a LiteShape, should add parameters to handle
     * the scale and to perform a scale dependent generalization !
     * 
     * @param sds the data source
     * @param fid the feature id
     * @return a liteshape representing the spatial feature
     * @throws ParameterException
     * @throws IOException
     * @throws DriverException
     * @todo fix maxDist for generalization!
     */
    public Shape getShape(SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {
        Geometry geom = this.getTheGeom(sds, fid); // geom + function

        MapTransform mt = MapEnv.getMapTransform();
        
        Shape shape = mt.getShape(geom, true);

        if (transform != null) {
            shape = transform.getGraphicalAffineTransform(sds, fid, true).createTransformedShape(shape);
        }

        return shape;
    }

    public Point2D getPointShape(SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {
        Geometry geom = this.getTheGeom(sds, fid); // geom + function

        MapTransform mt = MapEnv.getMapTransform();

        AffineTransform at = mt.getAffineTransform();
        if (transform != null) {
            at.preConcatenate(transform.getGraphicalAffineTransform(sds, fid, true));
        }

        Point point = geom.getCentroid();
        return at.transform(new Point2D.Double(point.getX(), point.getY()), null);
    }

    public Transform getTransform() {
        return transform;
    }

    @Override
    public Uom getUom() {
        return uom;
    }

    public void setUom(Uom uom) {
        if (uom == null) {
            this.uom = uom;
        } else {
            this.uom = Uom.MM;
        }
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        transform.setParent(this);
    }

    protected Transform transform;
    protected Uom uom;
}