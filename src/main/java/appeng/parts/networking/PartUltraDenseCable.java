package appeng.parts.networking;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.helpers.Reflected;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class PartUltraDenseCable extends PartCable {

    @Reflected
    public PartUltraDenseCable( final ItemStack is )
    {
        super( is );

        this.getProxy().setFlags( GridFlags.ULTRA_DENSE_CAPACITY, GridFlags.PREFERRED );
    }

    @Override
    public BusSupport supportsBuses()
    {
        return BusSupport.ULTRA_DENSE_CABLE;
    }

    @Override
    public void getBoxes( final IPartCollisionHelper bch )
    {
        final boolean noLadder = !bch.isBBCollision();
        final double min = noLadder ? 3.0 : 4.9;
        final double max = noLadder ? 13.0 : 11.1;

        bch.addBox( min, min, min, max, max, max );

        if( Platform.isServer() )
        {
            final IGridNode n = this.getGridNode();
            if( n != null )
            {
                this.setConnections( n.getConnectedSides() );
            }
            else
            {
                this.getConnections().clear();
            }
        }

        for( final AEPartLocation of : this.getConnections() )
        {
            if( this.isDense( of ) )
            {
                switch( of )
                {
                    case DOWN:
                        bch.addBox( min, 0.0, min, max, min, max );
                        break;
                    case EAST:
                        bch.addBox( max, min, min, 16.0, max, max );
                        break;
                    case NORTH:
                        bch.addBox( min, min, 0.0, max, max, min );
                        break;
                    case SOUTH:
                        bch.addBox( min, min, max, max, max, 16.0 );
                        break;
                    case UP:
                        bch.addBox( min, max, min, max, 16.0, max );
                        break;
                    case WEST:
                        bch.addBox( 0.0, min, min, min, max, max );
                        break;
                    default:
                }
            }
            else
            {
                switch( of )
                {
                    case DOWN:
                        bch.addBox( 5.0, 0.0, 5.0, 11.0, 5.0, 11.0 );
                        break;
                    case EAST:
                        bch.addBox( 11.0, 5.0, 5.0, 16.0, 11.0, 11.0 );
                        break;
                    case NORTH:
                        bch.addBox( 5.0, 5.0, 0.0, 11.0, 11.0, 5.0 );
                        break;
                    case SOUTH:
                        bch.addBox( 5.0, 5.0, 11.0, 11.0, 11.0, 16.0 );
                        break;
                    case UP:
                        bch.addBox( 5.0, 11.0, 5.0, 11.0, 16.0, 11.0 );
                        break;
                    case WEST:
                        bch.addBox( 0.0, 5.0, 5.0, 5.0, 11.0, 11.0 );
                        break;
                    default:
                }
            }
        }
    }

    private boolean isDense( final AEPartLocation of )
    {
        final TileEntity te = this.getTile().getWorld().getTileEntity( this.getTile().getPos().offset( of.getFacing() ) );

        if( te instanceof IGridHost)
        {
            final AECableType t = ( (IGridHost) te ).getCableConnectionType( of.getOpposite() );
            return t.isDense();
        }

        return false;
    }

    @MENetworkEventSubscribe
    public void channelUpdated( final MENetworkChannelsChanged c )
    {
        this.getHost().markForUpdate();
    }

    @MENetworkEventSubscribe
    public void powerRender( final MENetworkPowerStatusChange c )
    {
        this.getHost().markForUpdate();
    }
}
