package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityGolfCart extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    static
    {
        VehicleProperties properties = new VehicleProperties();
        properties.setAxleOffset(-0.5F);
        properties.setWheelOffset(4.45F);
        properties.setBodyPosition(new PartPosition(0, 0, 0, 0, 0, 0, 1.15));
        properties.setFuelPortPosition(new PartPosition(-13.25, 3.5, -7.3, 0, -90, 0, 0.25));
        properties.setKeyPortPosition(new PartPosition(-8.5, 2.75, 8.5, -67.5, 0, 0, 0.5));
        properties.setHeldOffset(new Vec3d(1.5D, 2.5D, 0.0D));
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.75F);
        properties.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F);
        properties.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -12.5F, 1.75F);
        VehicleProperties.setProperties(EntityGolfCart.class, properties);
    }

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityGolfCart(World worldIn)
    {
        super(worldIn);
        this.setSize(2F, 1F);
        //TODO figure out electric vehicles
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.GOLF_CART_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.electricEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.electricEngineStereo;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.ELECTRIC_MOTOR;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.6F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.4F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 11 * 0.0625;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            float xOffset = -0.3875F;
            float yOffset = (float)((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());
            float zOffset = 0.4F;

            if (this.getPassengers().size() > 0)
            {
                int index = this.getPassengers().indexOf(passenger);
                if (index > 0)
                {
                    xOffset -= (index / 2) * 0.6875F;
                    zOffset -= (index % 2) * 0.8F;
                }

                Vec3d vec3d = (new Vec3d(xOffset, 0.0D, zOffset)).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float)Math.PI / 2F));
                passenger.setPosition(this.posX + vec3d.x, this.posY + (double)yOffset, this.posZ + vec3d.z);
                passenger.rotationYaw -= deltaYaw;
                passenger.setRotationYawHead(passenger.rotationYaw);
                this.applyYawToEntity(passenger, index > 1);
            }
        }
    }

    private void applyYawToEntity(Entity entityToUpdate, boolean isBackSeat)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw - this.additionalYaw + (isBackSeat ? 180F : 0F));
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw + (isBackSeat ? 180F : 0F));
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        int index = this.getPassengers().indexOf(entityToUpdate);
        this.applyYawToEntity(entityToUpdate, index > 1);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 4;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
