package krowchat.UI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class kImageView extends ImageView
{
    public kImageView(String directory)
    {
        setImage(new Image(directory));
    }
    
    public void ResizeImage(boolean unused, double height)
    {
        setFitHeight(height);
    }
    
    public void ResizeImage(double width, boolean unused)
    {
        setFitWidth(width);
    }
    
    public void ResizeImage(double width, double height)
    {
        setFitWidth(width);
        setFitHeight(height);
    }
}
